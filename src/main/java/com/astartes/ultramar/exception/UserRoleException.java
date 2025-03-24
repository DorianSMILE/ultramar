package com.astartes.ultramar.exception;

public class UserRoleException extends RuntimeException {
    public UserRoleException(Long id) {
      super("Role with id "+id+" not found");
    }
}