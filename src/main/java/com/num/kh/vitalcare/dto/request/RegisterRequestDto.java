package com.num.kh.vitalcare.dto.request;

import com.num.kh.vitalcare.common.enumz.GenderType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Pattern(
      regexp = "^[a-zA-Z0-9_-]+$",
      message = "Username can only contain letters, numbers, underscores and hyphens")
  private String username;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 120, message = "Email must not exceed 120 characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
      message =
          "Password must contain at least one uppercase letter, one lowercase letter, and one number")
  private String password;

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  private String name;

  @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid")
  private String phoneNumber;

  @Min(value = 13, message = "Age must be at least 13")
  @Max(value = 120, message = "Age must not exceed 120")
  private Integer age;

  private GenderType gender;
}
