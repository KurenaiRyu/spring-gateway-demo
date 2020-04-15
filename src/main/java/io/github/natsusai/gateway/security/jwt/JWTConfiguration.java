package io.github.natsusai.gateway.security.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liufuhong
 * @since 2020-04-14 15:27
 */

@Configuration
@EnableConfigurationProperties(JWTProperties.class)
public class JWTConfiguration {

  @Bean
  public TokenProvider tokenProvider(JWTProperties jwtProperties) {
    return new TokenProvider(jwtProperties);
  }

}
