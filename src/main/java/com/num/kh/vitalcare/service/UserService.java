package com.num.kh.vitalcare.service;

import com.num.kh.vitalcare.dto.request.Oauth2CreateRequestDto;
import com.num.kh.vitalcare.dto.request.RegisterRequestDto;
import com.num.kh.vitalcare.dto.request.UserUpdateRequestDto;
import com.num.kh.vitalcare.dto.response.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  UserResponseDto registerLocalUser(RegisterRequestDto requestDto);

  UserResponseDto findOrCreateOAuth2User(Oauth2CreateRequestDto requestDto);

  UserResponseDto updateUser(Long userId, UserUpdateRequestDto requestDto);

  UserResponseDto getUserById(Long id);

  UserResponseDto getUserByUsername(String username);

  UserResponseDto getUserByEmail(String email);

  UserResponseDto getUserByUsernameOrEmail(String usernameOrEmail);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
