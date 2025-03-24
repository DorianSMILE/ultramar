package com.astartes.ultramar.exception;

public class UltramarineNotFoundException extends RuntimeException {
    public UltramarineNotFoundException(int id) {
        super("Ultramarine with ID "+id+" not found");
    }
}

