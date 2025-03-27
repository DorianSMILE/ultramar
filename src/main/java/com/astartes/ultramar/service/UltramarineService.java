package com.astartes.ultramar.service;

import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.entity.Ultramarine;
import com.astartes.ultramar.exception.UltramarineCreationException;
import com.astartes.ultramar.exception.UltramarineDeleteException;
import com.astartes.ultramar.exception.UltramarineNotFoundException;
import com.astartes.ultramar.exception.UltramarineUpdateException;
import com.astartes.ultramar.mapper.UltramarineMapper;
import com.astartes.ultramar.repository.UltramarineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UltramarineService {

    private final UltramarineRepository repository;

    public UltramarineService(UltramarineRepository repository) {
        this.repository = repository;
    }

    /**
     * Créer un Ultramarine
     */
    public UltramarineDTO create(UltramarineDTO dto) {
        try {
            Ultramarine entity = UltramarineMapper.toEntity(dto);
            entity = repository.save(entity);
            return UltramarineMapper.toDTO(entity);
        } catch (Exception e) {
            throw new UltramarineCreationException("Error creating Ultramarine");
        }
    }

    /**
     * Récupérer tous les Ultramarines
     * @return
     */
    public List<UltramarineDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(UltramarineMapper::toDTO)
                .toList();
    }

    /**
     * Récupérer tous les Ultramarines par name
     * @param name
     * @return
     */
    public List<UltramarineDTO> getAllByName(String name) {
        return repository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(UltramarineMapper::toDTO)
                .toList();
    }

    /**
     * Récupérer un Ultramarine par ID
     * @param id
     * @return
     */
    public UltramarineDTO getById(int id) {
        return repository.findById(id)
                .map(UltramarineMapper::toDTO)
                .orElseThrow(() -> new UltramarineNotFoundException(id));
    }

    /**
     * Mettre à jour un Ultramarine
     * @param id
     * @param dto
     * @return
     */
    public UltramarineDTO update(int id, UltramarineDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setName(dto.name());
            existing.setGrade(dto.grade());
            repository.save(existing);
            return UltramarineMapper.toDTO(existing);
        }).orElseThrow(() -> new UltramarineUpdateException(id));
    }

    /**
     * Supprimer un Ultramarine
     * @param id
     * @return
     */
    public void delete(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new UltramarineDeleteException(id);
        }
    }
}
