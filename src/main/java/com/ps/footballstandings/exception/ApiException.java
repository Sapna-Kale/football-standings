package com.ps.footballstandings.exception;

public class ApiException extends RuntimeException {
  public ApiException(String message) {
    super(message);
  }
}
