package com.astartes.ultramar.repository;

import com.astartes.ultramar.entity.EquipmentAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentAuthorizationRepository extends JpaRepository<EquipmentAuthorization, Long> {
    EquipmentAuthorization findByUltramarineIdAndCategory(Long ultramarineId, String category);
    List<EquipmentAuthorization> findByUltramarineId(Long ultramarineId);
    void deleteAllByUltramarineId(Long ultramarineId);
    boolean existsByUltramarineId(Long ultramarineId);
}
