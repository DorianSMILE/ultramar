package com.astartes.ultramar.exception;

public class EquipmentNotFoundException extends RuntimeException {
    public EquipmentNotFoundException(String message) {
        super(String.valueOf(message));
    }
}
