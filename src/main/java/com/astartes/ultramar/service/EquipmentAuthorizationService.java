package com.astartes.ultramar.service;


import com.astartes.ultramar.entity.EquipmentAuthorization;
import com.astartes.ultramar.exception.EquipmentNotFoundException;
import com.astartes.ultramar.repository.EquipmentAuthorizationRepository;
import org.springframework.stereotype.Service;

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

}
