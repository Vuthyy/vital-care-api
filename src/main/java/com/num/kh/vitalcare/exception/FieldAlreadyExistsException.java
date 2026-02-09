package com.num.kh.vitalcare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FieldAlreadyExistsException extends RuntimeException {

  private final String fieldName;
  private final String fieldValue;

  public FieldAlreadyExistsException(String fieldName, String fieldValue) {
    super(String.format("%s already exists with value: '%s'", fieldName, fieldValue));
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}
