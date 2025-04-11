package com.astartes.ultramar.exception;

public class EquipmentNotAuthorizedException extends RuntimeException {
    public EquipmentNotAuthorizedException(String message) {
        super(message);
    }
}
