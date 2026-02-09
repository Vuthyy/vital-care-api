package com.num.kh.vitalcare.service.impl;

import com.num.kh.vitalcare.common.enumz.AuthProviderType;
import com.num.kh.vitalcare.dto.request.Oauth2CreateRequestDto;
import com.num.kh.vitalcare.dto.request.RegisterRequestDto;
import com.num.kh.vitalcare.dto.request.UserUpdateRequestDto;
import com.num.kh.vitalcare.dto.response.UserResponseDto;
import com.num.kh.vitalcare.entity.UserEntity;
import com.num.kh.vitalcare.exception.FieldAlreadyExistsException;
import com.num.kh.vitalcare.exception.ResourceNotFoundException;
import com.num.kh.vitalcare.mapper.UserMapper;
import com.num.kh.vitalcare.repository.UserRepository;
import com.num.kh.vitalcare.service.UserService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserResponseDto registerLocalUser(RegisterRequestDto requestDto) {
    log.debug("Registering new local user: {}", requestDto.getUsername());

    if (existsByUsername(requestDto.getUsername())) {
      throw new FieldAlreadyExistsException("Username", requestDto.getUsername());
    }

    if (existsByEmail(requestDto.getEmail())) {
      throw new FieldAlreadyExistsException("Email", requestDto.getEmail());
    }

    UserEntity newUser = userMapper.toEntity(requestDto);
    newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
    newUser.setProvider(AuthProviderType.LOCAL);
    newUser.setEnabled(true);
    newUser.setAccountNonLocked(true);

    UserEntity savedUser = userRepository.save(newUser);
    log.info("Successfully registered user: {}", savedUser.getUsername());

    return userMapper.to(savedUser);
  }

  @Override
  @Transactional
  public UserResponseDto findOrCreateOAuth2User(Oauth2CreateRequestDto requestDto) {
    log.debug("Finding or creating OAuth2 user with email: {}", requestDto.getEmail());

    if (!StringUtils.hasText(requestDto.getEmail())) {
      throw new IllegalArgumentException("Email cannot be empty for OAuth2 user");
    }

    return userRepository
        .findByEmail(requestDto.getEmail())
        .map(
            user -> {
              log.debug("Found existing OAuth2 user: {}", user.getUsername());
              return userMapper.to(user);
            })
        .orElseGet(
            () -> {
              log.debug("Creating new OAuth2 user for email: {}", requestDto.getEmail());

              String baseUsername = requestDto.getEmail().split("@")[0];
              String username = generateUniqueUsername(baseUsername);

              UserEntity newUser =
                  UserEntity.builder()
                      .username(username)
                      .email(requestDto.getEmail())
                      .password(null)
                      .provider(
                          AuthProviderType.valueOf(requestDto.getProviderName().toUpperCase()))
                      .name(requestDto.getName())
                      .avatarUrl(requestDto.getAvatarUrl())
                      .enabled(true)
                      .accountNonLocked(true)
                      .build();

              UserEntity savedUser = userRepository.save(newUser);
              log.info("Successfully created OAuth2 user: {}", savedUser.getUsername());

              return userMapper.to(savedUser);
            });
  }

  @Override
  @Transactional
  public UserResponseDto updateUser(Long userId, UserUpdateRequestDto requestDto) {
    log.debug("Updating user: {}", userId);

    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

    userMapper.updateEntityFromDto(requestDto, user);

    UserEntity updatedUser = userRepository.save(user);
    log.info("Successfully updated user: {}", updatedUser.getUsername());

    return userMapper.to(updatedUser);
  }

  @Override
  public UserResponseDto getUserById(Long id) {
    log.debug("Loading user by id: {}", id);

    UserEntity user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

    return userMapper.to(user);
  }

  @Override
  public UserResponseDto getUserByUsername(String username) {
    log.debug("Loading user by username: {}", username);

    UserEntity user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

    return userMapper.to(user);
  }

  @Override
  public UserResponseDto getUserByEmail(String email) {
    log.debug("Loading user by email: {}", email);

    UserEntity user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

    return userMapper.to(user);
  }

  @Override
  public UserResponseDto getUserByUsernameOrEmail(String usernameOrEmail) {
    log.debug("Loading user by username or email: {}", usernameOrEmail);

    UserEntity user =
        userRepository
            .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(
                () -> new ResourceNotFoundException("User", "username or email", usernameOrEmail));

    return userMapper.to(user);
  }

  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("Loading UserDetails for username: {}", username);

    UserEntity user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    return User.builder()
        .username(user.getUsername())
        .password(user.getPassword() != null ? user.getPassword() : "")
        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        .accountLocked(!user.getAccountNonLocked())
        .disabled(!user.getEnabled())
        .build();
  }

  private String generateUniqueUsername(String baseUsername) {
    String candidate = baseUsername;
    int counter = 1;

    while (existsByUsername(candidate)) {
      candidate = baseUsername + counter++;
    }

    return candidate;
  }
}
