package com.astartes.ultramar.exception;

public class UltramarineNotFoundException extends RuntimeException {
    public UltramarineNotFoundException(Long id) {
        super("Ultramarine with ID "+id+" not found");
    }
}

