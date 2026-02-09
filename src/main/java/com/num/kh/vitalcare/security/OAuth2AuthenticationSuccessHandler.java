package com.num.kh.vitalcare.security;

import com.num.kh.vitalcare.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtService jwtService;

  @Value("${app.oauth2.authorized-redirect-uris:http://localhost:3000/oauth2/callback}")
  private String defaultRedirectUri;

  @Value("${app.oauth2.cookie-expire-seconds:180}")
  private int cookieExpireSeconds;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {

    log.info("OAuth2 authentication successful");

    if (response.isCommitted()) {
      log.warn("Response has already been committed. Unable to redirect to OAuth2 success URL");
      return;
    }

    try {
      // Extract OAuth2User from authentication
      OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
      String username = (String) oAuth2User.getAttributes().get("username");

      if (username == null || username.isBlank()) {
        log.error("Username not found in OAuth2User attributes");
        redirectToError(response, "Authentication failed: username not found");
        return;
      }

      log.debug("Generating tokens for OAuth2 user: {}", username);

      // Generate JWT tokens
      String accessToken = jwtService.generateAccessToken(username);
      String refreshToken = jwtService.generateRefreshToken(username);

      // Get redirect URI from request or use default
      String redirectUri = determineTargetUrl(request);

      // Validate redirect URI (security check)
      if (!isAuthorizedRedirectUri(redirectUri)) {
        log.error("Unauthorized redirect URI: {}", redirectUri);
        redirectUri = defaultRedirectUri;
      }

      // Build redirect URL with tokens
      String targetUrl =
          UriComponentsBuilder.fromUriString(redirectUri)
              .queryParam("token", URLEncoder.encode(accessToken, StandardCharsets.UTF_8))
              .build()
              .toUriString();

      // Set refresh token as HTTP-only cookie
      addRefreshTokenCookie(response, refreshToken);

      log.info("Redirecting OAuth2 user to: {}", redirectUri);
      response.sendRedirect(targetUrl);

    } catch (Exception e) {
      log.error("Error during OAuth2 authentication success handling", e);
      redirectToError(response, "Authentication failed: " + e.getMessage());
    }
  }

  private String determineTargetUrl(HttpServletRequest request) {
    // Try to get from request parameter
    String redirectUri = request.getParameter("redirect_uri");

    if (redirectUri != null && !redirectUri.isBlank()) {
      return redirectUri;
    }

    // Try to get from saved request (if using HttpSessionOAuth2AuthorizationRequestRepository)
    // This could be enhanced to use a custom repository to save the redirect URI

    // Fall back to default
    return defaultRedirectUri;
  }

  private boolean isAuthorizedRedirectUri(String uri) {
    // In production, you should validate against a whitelist
    // For now, we'll allow localhost and your production domains
    return uri.startsWith("http://localhost:")
        || uri.startsWith("https://localhost:")
        || uri.startsWith(defaultRedirectUri)
        || uri.startsWith("https://yourdomain.com"); // Replace with your actual domain
  }

  private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    Cookie cookie = new Cookie("refresh_token", refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // Set to true in production with HTTPS
    cookie.setPath("/");
    cookie.setMaxAge(cookieExpireSeconds);

    response.addCookie(cookie);
  }

  private void redirectToError(HttpServletResponse response, String errorMessage)
      throws IOException {
    String errorUrl =
        UriComponentsBuilder.fromUriString(defaultRedirectUri)
            .queryParam("error", URLEncoder.encode(errorMessage, StandardCharsets.UTF_8))
            .build()
            .toUriString();
    response.sendRedirect(errorUrl);
  }
}
