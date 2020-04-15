package io.github.natsusai.gateway.security.jwt;

import io.github.natsusai.gateway.security.AuthoritiesConstants;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * JWT认证管理器
 */
@Component
public class JWTReactiveAuthenticationManager implements ReactiveAuthenticationManager {

  private static final String PERMISSION_KEY = "permission";

  /**
   * Successfully authenticate an Authentication object
   *
   * @param authentication A valid authentication object
   * @return authentication A valid authentication object
   */
  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    //匿名用户直接跳过
    if (authentication instanceof AnonymousAuthenticationToken) {
      return Mono.just(authentication);
    }

    //TODO: 添加权限
    UserDetails userDetails = User.withUsername(authentication.getName())
        .password("")
        .authorities(AuthoritiesConstants.USER)
        .build();
    return Mono.just(
        new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(),
            userDetails.getAuthorities()));
  }
}
