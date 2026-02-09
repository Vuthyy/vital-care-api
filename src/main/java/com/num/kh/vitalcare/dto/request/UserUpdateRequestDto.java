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
public class UserUpdateRequestDto {

  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  private String name;

  @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid")
  private String phoneNumber;

  @Min(value = 13, message = "Age must be at least 13")
  @Max(value = 120, message = "Age must not exceed 120")
  private Integer age;

  private GenderType gender;

  @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
  private String avatarUrl;
}
