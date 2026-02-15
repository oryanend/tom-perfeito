package com.oryanend.tom_perfeito_api.services.exceptions;

public class DatabaseException extends RuntimeException {
  public DatabaseException(String message) {
    super(message);
  }
}
