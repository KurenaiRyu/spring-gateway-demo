package io.github.natsusai.gateway.security.jwt;

/**
 * JWT常量
 *
 * @author liufuhong
 * @since 2019-07-17 10:16
 */

public final class JWTConstants {
  public static final String BEARER_PREFIX      = "Bearer ";
  public static final String AUTH_CACHE_NAMESPACE = "AUTH_CACHE";
  public static final String ANONYMOUS_KEY      = "Anonymous";
  public static final String ANONYMOUS_USERNAME = "AnonymousUser";

  private JWTConstants() {
  }
}
