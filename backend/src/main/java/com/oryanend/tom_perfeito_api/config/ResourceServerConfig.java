package com.oryanend.tom_perfeito_api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

  @Value("${cors.origins}")
  private String corsOrigins;

  // make this optional with empty default to avoid missing-placeholder errors in some envs
  @Value("${app.cors.allowed-origins:}")
  private String angularOrigin;

  @Bean
  @Profile("test")
  @Order(1)
  public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {

    http.securityMatcher(PathRequest.toH2Console())
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
    return http.build();
  }

  @Bean
  @Order(3)
  public SecurityFilterChain rsSecurityFilterChain(HttpSecurity http) throws Exception {
    http.cors(Customizer.withDefaults());
    http.csrf(AbstractHttpConfigurer::disable);
    http.authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll());
    http.oauth2ResourceServer(
        oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
        new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
    grantedAuthoritiesConverter.setAuthorityPrefix("");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    List<String> allowed = getStrings();
    CorsConfiguration corsConfig = new CorsConfiguration();

    if (!allowed.isEmpty()) {
      corsConfig.setAllowedOriginPatterns(allowed);
      corsConfig.setAllowedOrigins(allowed);
    } else {
      corsConfig.setAllowedOriginPatterns(List.of("*"));
    }

    corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH", "OPTIONS"));
    corsConfig.setAllowCredentials(true);
    corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);
    return source;
  }

  private List<String> getStrings() {
    List<String> allowed = new ArrayList<>();

    if (corsOrigins != null && !corsOrigins.isBlank()) {
      String[] origins = corsOrigins.split(",");
      for (String o : origins) {
        String trimmed = o.trim();
        if (!trimmed.isEmpty()) {
          allowed.add(trimmed);
        }
      }
    }

    if (angularOrigin != null && !angularOrigin.isBlank()) {
      String[] ao = angularOrigin.split(",");
      for (String o : ao) {
        String trimmed = o.trim();
        if (!trimmed.isEmpty() && !allowed.contains(trimmed)) {
          allowed.add(trimmed);
        }
      }
    }

    return allowed;
  }

  @Bean
  FilterRegistrationBean<CorsFilter> corsFilter() {
    FilterRegistrationBean<CorsFilter> bean =
        new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }
}
