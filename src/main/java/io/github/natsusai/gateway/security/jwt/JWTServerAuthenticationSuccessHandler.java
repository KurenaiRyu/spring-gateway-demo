package io.github.natsusai.gateway.security.jwt;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

/**
 * @author liufuhong
 * @since 2020-04-15 14:30
 */

public class JWTServerAuthenticationSuccessHandler extends
    WebFilterChainServerAuthenticationSuccessHandler {

  private static final String X_JWT_SUB_HEADER = "X-jwt-sub";

  @Override
  public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
      Authentication authentication) {
    //TODO: 将相关属性设置进header
    webFilterExchange.getExchange().getRequest().mutate()
        .header(X_JWT_SUB_HEADER , authentication.getName())
        .build();
    return super.onAuthenticationSuccess(webFilterExchange, authentication);
  }
}
