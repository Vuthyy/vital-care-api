package com.num.kh.vitalcare.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

  @Value("${app.oauth2.authorized-redirect-uris:http://localhost:3000/oauth2/callback}")
  private String defaultRedirectUri;

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {

    log.error("OAuth2 authentication failed: {}", exception.getMessage());

    String redirectUri = request.getParameter("redirect_uri");
    if (redirectUri == null || redirectUri.isBlank()) {
      redirectUri = defaultRedirectUri;
    }

    String targetUrl =
        UriComponentsBuilder.fromUriString(redirectUri)
            .queryParam(
                "error", URLEncoder.encode(exception.getLocalizedMessage(), StandardCharsets.UTF_8))
            .build()
            .toUriString();

    response.sendRedirect(targetUrl);
  }
}
