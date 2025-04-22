package com.astartes.ultramar.service;

import com.astartes.ultramar.DTO.EquipmentAuthorizationDTO;
import com.astartes.ultramar.DTO.UltramarineSelectDTO;
import com.astartes.ultramar.entity.EquipmentAuthorization;
import com.astartes.ultramar.entity.Ultramarine;
import com.astartes.ultramar.enumeration.SupplyEnum;
import com.astartes.ultramar.enumeration.WeightEnum;
import com.astartes.ultramar.exception.EquipmentNotFoundException;
import com.astartes.ultramar.repository.EquipmentAuthorizationRepository;
import com.astartes.ultramar.repository.UltramarineRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipmentAuthorizationService {

    private final EquipmentAuthorizationRepository authRepository;
    private final UltramarineRepository ultramarineRepository;

    public EquipmentAuthorizationService(EquipmentAuthorizationRepository authRepository, UltramarineRepository ultramarineRepository) {
        this.authRepository = authRepository;
        this.ultramarineRepository = ultramarineRepository;
    }

    /**
     * Vérifie si l'ultramarine est autorisé à porter un équipement de la catégorie indiquée,
     * en comparant le nombre courant d'équipements portés avec le nombre autorisé.
     */
    public boolean isAuthorized(Long ultramarineId, String category, int currentCount) {
        EquipmentAuthorization auth = authRepository.findByUltramarineIdAndCategory(ultramarineId, category);
        if (auth == null) {
            throw new EquipmentNotFoundException("Aucune autorisation trouvée pour l'ultramarine " + ultramarineId + " et la catégorie " + category);
        }
        if (auth.getNbAuthorized() == null) {
            return false; // illimité → jamais bloquant
        }
        return currentCount > auth.getNbAuthorized();
    }

    /**
     * Retourne les autorisations (supply & weight) pour un ultramarine donné, formatées en DTO.
     */
    public EquipmentAuthorizationDTO getAuthorizationsForUltramarine(long ultramarineId) {
        List<EquipmentAuthorization> auths = authRepository.findByUltramarineId(ultramarineId);
        return toDTO(ultramarineId, auths);
    }

    public List<EquipmentAuthorizationDTO> findAll() {
        List<EquipmentAuthorization> all = authRepository.findAll();

        return all.stream()
                .collect(Collectors.groupingBy(auth -> auth.getUltramarine().getId()))
                .entrySet().stream()
                .map(entry -> toDTO((long) entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }


    public Optional<EquipmentAuthorizationDTO> findById(Long id) {
        return authRepository.findById(id)
                .map(auth -> {
                    Long umId = (long) auth.getUltramarine().getId();
                    List<EquipmentAuthorization> auths = authRepository.findByUltramarineId(umId);
                    return toDTO(umId, auths);
                });
    }

    public List<EquipmentAuthorizationDTO> findByUltramarineId(Long umId) {
        List<EquipmentAuthorization> auths = authRepository.findByUltramarineId(umId);
        if (auths.isEmpty()) return Collections.emptyList();
        return List.of(toDTO(umId, auths));
    }

    @Transactional
    public Optional<EquipmentAuthorizationDTO> updateAuthorization(EquipmentAuthorizationDTO dto) {
        Long id = dto.getUltramarineId();
        authRepository.deleteAllByUltramarineId(id);
        List<EquipmentAuthorization> newAuthorizations = toEntities(dto);
        List<EquipmentAuthorization> saved = authRepository.saveAll(newAuthorizations);
        return Optional.of(toDTO(dto.getUltramarineId(), saved));
    }

    public void delete(Long id) {
        authRepository.deleteById(id);
    }

    /**
     * Méthode interne réutilisable pour transformer une liste d'autorisations en DTO,
     * en complétant les manquants avec "unautorized" ou "illimité".
     */
    private EquipmentAuthorizationDTO toDTO(Long ultramarineId, List<EquipmentAuthorization> auths) {
        Map<String, String> supplyMap = new LinkedHashMap<>();
        Map<String, String> weightMap = new LinkedHashMap<>();

        for (SupplyEnum s : SupplyEnum.values()) {
            EquipmentAuthorization auth = auths.stream()
                    .filter(a -> a.getCategory().equals(s.name()))
                    .findFirst()
                    .orElse(null);
            supplyMap.put(s.name(), auth == null ? "unautorized" :
                    auth.getNbAuthorized() == null ? "unlimited" : auth.getNbAuthorized().toString());
        }

        for (WeightEnum w : WeightEnum.values()) {
            EquipmentAuthorization auth = auths.stream()
                    .filter(a -> a.getCategory().equals(w.name()))
                    .findFirst()
                    .orElse(null);
            weightMap.put(w.name(), auth == null ? "unautorized" :
                    auth.getNbAuthorized() == null ? "unlimited" : auth.getNbAuthorized().toString());
        }

        return new EquipmentAuthorizationDTO(ultramarineId, supplyMap, weightMap);
    }

    private List<EquipmentAuthorization> toEntities(EquipmentAuthorizationDTO dto) {
        List<EquipmentAuthorization> entities = new ArrayList<>();

        Ultramarine ultramarine = ultramarineRepository.findById(dto.getUltramarineId())
                .orElseThrow(() -> new EntityNotFoundException("Ultramarine not found with ID: " + dto.getUltramarineId()));

        dto.getWeightAuthorizations().forEach((category, value) -> {
            if ("unautorized".equals(value)) return;

            Long parsedValue = null;
            if (!"unlimited".equals(value)) {
                try {
                    parsedValue = Long.parseLong(value);
                    if (parsedValue <= 0) {
                        throw new IllegalArgumentException("La valeur pour la catégorie " + category + " doit être supérieure à 0.");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Valeur invalide pour la catégorie " + category + " : " + value);
                }
            }

            entities.add(createEntity(ultramarine, category, parsedValue));
        });

        dto.getSupplyAuthorizations().forEach((category, value) -> {
            if ("unautorized".equals(value)) return;

            Long parsedValue = null;
            if (!"unlimited".equals(value)) {
                try {
                    parsedValue = Long.parseLong(value);
                    if (parsedValue <= 0) {
                        throw new IllegalArgumentException("La valeur pour la catégorie " + category + " doit être supérieure à 0.");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Valeur invalide pour la catégorie " + category + " : " + value);
                }
            }

            entities.add(createEntity(ultramarine, category, parsedValue));
        });

        return entities;
    }

    private EquipmentAuthorization createEntity(Ultramarine ultramarine, String fullCategory, Long parsedValue) {
        EquipmentAuthorization auth = new EquipmentAuthorization();
        auth.setUltramarine(ultramarine);
        auth.setCategory(fullCategory);

        if (parsedValue == null) {
            auth.setNbAuthorized(null);
        } else {
            auth.setNbAuthorized(parsedValue.intValue());
        }

        return auth;
    }

    public List<UltramarineSelectDTO> findUltramarinesWithoutAuthorization() {
        List<Ultramarine> all = ultramarineRepository.findAll();
        return all.stream()
                .filter(this::hasNoAuthorization)
                .map(um -> new UltramarineSelectDTO(um.getId(), um.getName()))
                .toList();
    }

    private boolean hasNoAuthorization(Ultramarine um) {
        return !authRepository.existsByUltramarineId(um.getId());
    }

    @Transactional
    public void deleteAllAuthorizationUltramarine(Long ultramarineId) {
        authRepository.deleteAllByUltramarineId(ultramarineId);
    }

}
