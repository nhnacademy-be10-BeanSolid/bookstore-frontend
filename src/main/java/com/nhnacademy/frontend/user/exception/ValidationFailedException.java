package com.nhnacademy.frontend.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationFailedException extends RuntimeException {
  public ValidationFailedException(BindingResult bindingResult) {
    super(buildErrorMessage(bindingResult));
  }

  private static String buildErrorMessage(BindingResult bindingResult) {
    return bindingResult.getFieldErrors().stream()
            .map(error -> String.format(
                    "field='%s', rejectedValue='%s', message='%s', code='%s'",
                    error.getField(),
                    error.getRejectedValue(),
                    error.getDefaultMessage(),
                    error.getCode()
            ))
            .collect(Collectors.joining("\n"));
  }
}
