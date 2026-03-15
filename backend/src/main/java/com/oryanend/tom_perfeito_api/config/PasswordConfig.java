package com.oryanend.tom_perfeito_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

  @Value("${security.bcrypt.strength}")
  private int strength;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(strength);
  }
}
