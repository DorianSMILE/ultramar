package com.astartes.ultramar.service;


import com.astartes.ultramar.DTO.UltramarineAuthorizationDTO;
import com.astartes.ultramar.entity.EquipmentAuthorization;
import com.astartes.ultramar.enumeration.SupplyEnum;
import com.astartes.ultramar.enumeration.WeightEnum;
import com.astartes.ultramar.exception.EquipmentNotFoundException;
import com.astartes.ultramar.repository.EquipmentAuthorizationRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EquipmentAuthorizationService {

    private final EquipmentAuthorizationRepository authRepository;

    public EquipmentAuthorizationService(EquipmentAuthorizationRepository authRepository) {
        this.authRepository = authRepository;
    }

    /**
     * Vérifie si l'ultramarine est autorisé à porter un équipement de la catégorie indiquée,
     * en comparant le nombre courant d'équipements portés avec le nombre autorisé.
     *
     * @param ultramarineId l'ID de l'ultramarine
     * @param category La catégorie autorisée (correspond à une valeur de SupplyEnum ou WeightEnum)
     * @param currentCount Le nombre d'équipements déjà portés dans cette catégorie
     * @return true si l'ajout d'un équipement est autorisé, false sinon
     */
    public boolean isAuthorized(int ultramarineId, String category, int currentCount) {
        EquipmentAuthorization auth = authRepository.findByUltramarineIdAndCategory(ultramarineId, category);
        if (auth == null) {
            // Aucun enregistrement pour cette catégorie signifie l'absence d'autorisation.
            throw new EquipmentNotFoundException("Aucune autorisation trouvée pour l'ultramarine " + ultramarineId + " et la catégorie " + category);
        }
        if (auth.getNbAuthorized() == null) {
            // Null signifie que l'autorisation est illimitée.
            return false;
        }
        return currentCount > auth.getNbAuthorized();
    }

    public UltramarineAuthorizationDTO getAuthorizationsForUltramarine(int ultramarineId) {
        List<EquipmentAuthorization> auths = authRepository.findByUltramarineId(ultramarineId);

        // Pour conserver l'ordre, utilise LinkedHashMap
        Map<String, String> supplyMap = new LinkedHashMap<>();
        Map<String, String> weightMap = new LinkedHashMap<>();

        // Pour chaque valeur de SupplyEnum on détermine l'autorisation
        for (SupplyEnum s : SupplyEnum.values()) {
            EquipmentAuthorization auth = auths.stream()
                    .filter(a -> a.getCategory().equals(s.name()))
                    .findFirst()
                    .orElse(null);
            if (auth == null) {
                supplyMap.put(s.name(), "unautorized");
            } else {
                // Si nbAuthorized est null, on considère "illimité", sinon la valeur numérique
                supplyMap.put(s.name(), auth.getNbAuthorized() == null ? "illimité" : auth.getNbAuthorized().toString());
            }
        }

        // Pareil pour WeightEnum
        for (WeightEnum w : WeightEnum.values()) {
            EquipmentAuthorization auth = auths.stream()
                    .filter(a -> a.getCategory().equals(w.name()))
                    .findFirst()
                    .orElse(null);
            if (auth == null) {
                weightMap.put(w.name(), "unautorized");
            } else {
                weightMap.put(w.name(), auth.getNbAuthorized() == null ? "illimité" : auth.getNbAuthorized().toString());
            }
        }

        return new UltramarineAuthorizationDTO(ultramarineId, supplyMap, weightMap);
    }

}
