package com.num.kh.vitalcare.converter;

import com.num.kh.vitalcare.common.enumz.AuthProviderType;
import java.util.Map;

public class OAuth2AttributeConverter {

  // Normalize OAuth2 provider attributes into a minimal profile
  public record Profile(String email, String name, String avatarUrl) {}

  public static Profile from(AuthProviderType provider, Map<String, Object> attributes) {
    return switch (provider) {
      case GOOGLE ->
          new Profile(
              (String) attributes.get("email"),
              (String) attributes.getOrDefault("name", attributes.get("given_name")),
              (String) attributes.get("picture"));
      case FACEBOOK ->
          new Profile(
              (String) attributes.get("email"),
              (String) attributes.getOrDefault("name", "Facebook User"),
              // Facebook graph v12: picture is nested
              attributes.containsKey("picture")
                  ? (String)
                      ((Map<?, ?>) ((Map<?, ?>) attributes.get("picture")).get("data")).get("url")
                  : null);
      case LOCAL -> throw new IllegalArgumentException("LOCAL not OAuth2");
    };
  }
}
