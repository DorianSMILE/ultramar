package com.astartes.ultramar.repository;

import com.astartes.ultramar.entity.Ultramarine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UltramarineRepository extends JpaRepository<Ultramarine, Integer> {
    Optional<Ultramarine> findByName(String name);
}