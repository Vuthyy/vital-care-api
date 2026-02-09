package com.num.kh.vitalcare.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Oauth2CreateRequestDto {

  private String email;
  private String name;
  private String avatarUrl;
  private String providerName;
}
