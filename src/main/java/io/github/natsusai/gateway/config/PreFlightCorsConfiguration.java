package io.github.natsusai.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class PreFlightCorsConfiguration {

  @Bean
  public CorsWebFilter corsFilter(CorsConfigurationSource corsConfigurationSource) {
    return new CorsWebFilter(corsConfigurationSource);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
    config.addAllowedMethod(HttpMethod.POST);
    config.addAllowedMethod(HttpMethod.GET);
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}