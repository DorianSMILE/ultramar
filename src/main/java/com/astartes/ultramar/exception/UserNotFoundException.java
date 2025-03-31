package com.astartes.ultramar.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String uuidOrUsername) {
    super("User not found with given UUID or username : " + uuidOrUsername);
  }
}
