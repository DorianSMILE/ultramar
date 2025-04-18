package com.astartes.ultramar.exception;

// Exception pour la suppression échouée
public class UltramarineDeleteException extends RuntimeException {
    public UltramarineDeleteException(Long id) {
        super("Failed to delete Ultramarine with id " + id);
    }
}
