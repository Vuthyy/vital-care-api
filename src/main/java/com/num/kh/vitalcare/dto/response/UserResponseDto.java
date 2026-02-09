package com.num.kh.vitalcare.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.num.kh.vitalcare.common.enumz.AuthProviderType;
import com.num.kh.vitalcare.common.enumz.GenderType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

  private Long id;
  private String username;
  private String email;
  private String name;
  private String phoneNumber;
  private Integer age;
  private GenderType gender;
  private AuthProviderType provider;
  private String avatarUrl;
  private Boolean enabled;
  private Boolean accountNonLocked;
  private Instant createdOn;
  private Instant modifiedOn;
}
