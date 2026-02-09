package com.num.kh.vitalcare.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

  @Bean
  public AuditorAware<String> auditorProvider() {
    return new AuditorAwareImpl();
  }

  static class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null
          || !authentication.isAuthenticated()
          || authentication.getPrincipal().equals("anonymousUser")) {
        return Optional.of("system");
      }

      if (authentication.getPrincipal() instanceof UserDetails userDetails) {
        return Optional.of(userDetails.getUsername());
      }

      return Optional.of(authentication.getName());
    }
  }
}
