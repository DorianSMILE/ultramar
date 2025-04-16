package com.astartes.ultramar.service;

import com.astartes.ultramar.DTO.EquipmentAuthorizationDTO;
import com.astartes.ultramar.entity.EquipmentAuthorization;
import com.astartes.ultramar.enumeration.SupplyEnum;
import com.astartes.ultramar.enumeration.WeightEnum;
import com.astartes.ultramar.exception.EquipmentNotFoundException;
import com.astartes.ultramar.repository.EquipmentAuthorizationRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipmentAuthorizationService {

    private final EquipmentAuthorizationRepository authRepository;

    public EquipmentAuthorizationService(EquipmentAuthorizationRepository authRepository) {
        this.authRepository = authRepository;
    }

    /**
     * Vérifie si l'ultramarine est autorisé à porter un équipement de la catégorie indiquée,
     * en comparant le nombre courant d'équipements portés avec le nombre autorisé.
     */
    public boolean isAuthorized(int ultramarineId, String category, int currentCount) {
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
    public EquipmentAuthorizationDTO getAuthorizationsForUltramarine(int ultramarineId) {
        List<EquipmentAuthorization> auths = authRepository.findByUltramarineId(ultramarineId);
        return toDTO(ultramarineId, auths);
    }

    /**
     * Retourne toutes les autorisations groupées par ultramarine et formatées en DTO.
     */
    public List<EquipmentAuthorizationDTO> findAllDTO() {
        List<EquipmentAuthorization> all = authRepository.findAll();

        return all.stream()
                .collect(Collectors.groupingBy(EquipmentAuthorization::getUltramarineId))
                .entrySet().stream()
                .map(entry -> toDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<EquipmentAuthorizationDTO> findAll() {
        return authRepository.findAll().stream()
                .collect(Collectors.groupingBy(EquipmentAuthorization::getUltramarineId))
                .entrySet().stream()
                .map(entry -> toDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public Optional<EquipmentAuthorizationDTO> findById(Long id) {
        return authRepository.findById(id)
                .map(auth -> {
                    int umId = auth.getUltramarineId();
                    List<EquipmentAuthorization> auths = authRepository.findByUltramarineId(umId);
                    return toDTO(umId, auths);
                });
    }

    public List<EquipmentAuthorizationDTO> findByUltramarineId(int umId) {
        List<EquipmentAuthorization> auths = authRepository.findByUltramarineId(umId);
        if (auths.isEmpty()) return Collections.emptyList();
        return List.of(toDTO(umId, auths));
    }

    public EquipmentAuthorization save(EquipmentAuthorization auth) {
        return authRepository.save(auth);
    }

    public void delete(Long id) {
        authRepository.deleteById(id);
    }

    /**
     * Méthode interne réutilisable pour transformer une liste d'autorisations en DTO,
     * en complétant les manquants avec "unautorized" ou "illimité".
     */
    private EquipmentAuthorizationDTO toDTO(int ultramarineId, List<EquipmentAuthorization> auths) {
        Map<String, String> supplyMap = new LinkedHashMap<>();
        Map<String, String> weightMap = new LinkedHashMap<>();

        for (SupplyEnum s : SupplyEnum.values()) {
            EquipmentAuthorization auth = auths.stream()
                    .filter(a -> a.getCategory().equals(s.name()))
                    .findFirst()
                    .orElse(null);
            supplyMap.put(s.name(), auth == null ? "unautorized" :
                    auth.getNbAuthorized() == null ? "illimité" : auth.getNbAuthorized().toString());
        }

        for (WeightEnum w : WeightEnum.values()) {
            EquipmentAuthorization auth = auths.stream()
                    .filter(a -> a.getCategory().equals(w.name()))
                    .findFirst()
                    .orElse(null);
            weightMap.put(w.name(), auth == null ? "unautorized" :
                    auth.getNbAuthorized() == null ? "illimité" : auth.getNbAuthorized().toString());
        }

        return new EquipmentAuthorizationDTO(ultramarineId, supplyMap, weightMap);
    }
}
