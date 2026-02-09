package com.num.kh.vitalcare.security;

import com.num.kh.vitalcare.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  // Add @Lazy to break circular dependency
  public JwtAuthenticationFilter(
      JwtService jwtService, @Lazy UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String jwt = extractJwtFromRequest(request);

      if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if (jwtService.isTokenValid(jwt)) {
          String username = jwtService.extractUsername(jwt);

          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          if (!jwtService.isTokenExpired(jwt)) {
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Set Authentication for user: {}", username);
          } else {
            log.debug("JWT token is expired");
          }
        } else {
          log.debug("JWT token is invalid");
        }
      }
    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  private String extractJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }

    return null;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();

    return path.startsWith("/api/v1/auth/login")
        || path.startsWith("/api/v1/auth/register")
        || path.startsWith("/api/v1/auth/refresh")
        || path.startsWith("/oauth2/")
        || path.startsWith("/swagger-ui/")
        || path.startsWith("/v3/api-docs/")
        || path.startsWith("/actuator/health");
  }
}
