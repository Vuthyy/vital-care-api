package com.num.kh.vitalcare.service.impl;

import com.num.kh.vitalcare.common.enumz.AuthProviderType;
import com.num.kh.vitalcare.converter.OAuth2AttributeConverter;
import com.num.kh.vitalcare.dto.request.Oauth2CreateRequestDto;
import com.num.kh.vitalcare.service.OAuth2UserService;
import com.num.kh.vitalcare.service.UserService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService {

  private final UserService userService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.debug("Loading OAuth2 user");

    // Delegate to default OAuth2UserService
    DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    OAuth2User oauth2User = delegate.loadUser(userRequest);

    // Extract provider information
    String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
    AuthProviderType provider = AuthProviderType.valueOf(registrationId);
    Map<String, Object> attributes = oauth2User.getAttributes();

    log.debug("Processing OAuth2 user from provider: {}", provider);

    // Convert OAuth2 attributes to our domain profile
    var profile = OAuth2AttributeConverter.from(provider, attributes);

    // Create request DTO
    Oauth2CreateRequestDto requestDto =
        Oauth2CreateRequestDto.builder()
            .email(profile.email())
            .name(profile.name())
            .avatarUrl(profile.avatarUrl())
            .providerName(registrationId)
            .build();

    // Find or create user in our database
    var domainUser = userService.findOrCreateOAuth2User(requestDto);

    // Build custom attributes map
    Map<String, Object> customAttributes = new HashMap<>();
    customAttributes.put("username", domainUser.getUsername());
    customAttributes.put("email", domainUser.getEmail());
    customAttributes.put("name", domainUser.getName());

    if (domainUser.getAvatarUrl() != null) {
      customAttributes.put("avatarUrl", domainUser.getAvatarUrl());
    }

    log.info("Successfully authenticated OAuth2 user: {}", domainUser.getUsername());

    // Return DefaultOAuth2User with username as the principal name
    return new DefaultOAuth2User(
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
        customAttributes,
        "username");
  }
}
