package com.num.kh.vitalcare.dto.request;

import java.security.AuthProvider;
import lombok.Data;

@Data
public class UserRequestDto {

  private String username;
  private String email;
  private String password;
  private AuthProvider provider;
  private String name;
  private String avatarUrl;
}
