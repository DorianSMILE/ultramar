package com.astartes.ultramar.controller;

import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.service.UltramarineService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ultramarines")
public class UltramarineRestController {

    private final UltramarineService ultramarineService;

    public UltramarineRestController(UltramarineService ultramarineService) {
        this.ultramarineService = ultramarineService;
    }

    @GetMapping("/")
    public List<UltramarineDTO> getAll() {
        return ultramarineService.getAll();
    }

    @PostMapping("/create")
    public ResponseEntity<UltramarineDTO> createUltramarine(@Valid @RequestBody UltramarineDTO ultramarineDTO) {
        UltramarineDTO saved = ultramarineService.create(ultramarineDTO);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UltramarineDTO>> getAllByName(@RequestParam @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$") String name) {
        List<UltramarineDTO> results = ultramarineService.getAllByName(name);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UltramarineDTO> getById(@PathVariable @NotNull int id) {
        UltramarineDTO dto = ultramarineService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        ultramarineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
