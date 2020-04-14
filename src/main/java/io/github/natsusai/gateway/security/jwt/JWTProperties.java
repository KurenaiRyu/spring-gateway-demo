package io.github.natsusai.gateway.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liufuhong
 * @since 2020-04-14 15:24
 */

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JWTProperties {
  private String secret;
  private String base64Secret;
  private Long tokenValidityInSeconds;
  private Long tokenValidityInSecondsForRememberMe;
}
