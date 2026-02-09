package com.num.kh.vitalcare.exception;

import java.time.LocalDateTime;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {

  private LocalDateTime timestamp;
  private String message;
  private String path;
  private String errorCode;
}
