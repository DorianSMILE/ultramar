package com.astartes.ultramar.exception;

// Exception pour les erreurs de mise à jour
public class UltramarineUpdateException extends RuntimeException {
    public UltramarineUpdateException(int id) {
        super("Failed to update Ultramarine with id " + id);
    }
}
