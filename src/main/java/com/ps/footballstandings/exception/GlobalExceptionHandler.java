package com.ps.footballstandings.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
    return new ResponseEntity<>(
        new ErrorResponse(HttpStatus.BAD_GATEWAY.value(), "API Error: " + ex.getMessage()),
        HttpStatus.BAD_GATEWAY);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return new ResponseEntity<>(
        new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid parameter: " + ex.getName()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    return new ResponseEntity<>(
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error: " + ex.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
