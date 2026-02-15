package com.oryanend.tom_perfeito_api.db.migration.exceptions;

public class PathNotFoundException extends RuntimeException {
  public PathNotFoundException(String message) {
    super(message);
  }
}
