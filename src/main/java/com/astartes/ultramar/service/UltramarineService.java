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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UltramarineService {

    private final UltramarineRepository repository;
    private final UltramarineMapper ultramarineMapper;

    public UltramarineService(UltramarineRepository repository, UltramarineMapper ultramarineMapper) {
        this.repository = repository;
        this.ultramarineMapper = ultramarineMapper;
    }

    /**
     * Créer un Ultramarine
     */
    @Transactional
    public UltramarineDTO create(UltramarineDTO dto) {
        try {
            Ultramarine entity = ultramarineMapper.toEntity(dto);
            entity = repository.save(entity);
            return ultramarineMapper.toDTO(entity);
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
                .map(ultramarineMapper::toDTO)
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
                .map(ultramarineMapper::toDTO)
                .toList();
    }

    /**
     * Récupérer un Ultramarine par ID
     * @param id
     * @return
     */
    public UltramarineDTO getById(int id) {
        return repository.findById(id)
                .map(ultramarineMapper::toDTO)
                .orElseThrow(() -> new UltramarineNotFoundException(id));
    }

    /**
     * Mettre à jour un Ultramarine
     * @param dto
     * @return
     */
    @Transactional
    public UltramarineDTO updateUltramarine(UltramarineDTO dto) {
        return repository.findById(dto.id()).map(existing -> {
            existing.setName(dto.name());
            existing.setGrade(dto.grade());
            repository.saveAndFlush(existing);
            return ultramarineMapper.toDTO(existing);
        }).orElseThrow(() -> new UltramarineUpdateException(dto.id()));
    }

    /**
     * Supprimer un Ultramarine
     * @param id
     * @return
     */
    @Transactional
    public void delete(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new UltramarineDeleteException(id);
        }
    }
}
