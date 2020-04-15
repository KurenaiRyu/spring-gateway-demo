package io.github.natsusai.gateway.security;

import io.github.natsusai.gateway.util.SpringHelper;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * 权限认证管理器
 *
 * @author liufuhong
 * @since 2019-05-27 16:17
 */

@Slf4j
@Component
public class CustomReactiveAuthorizationManager
    implements ReactiveAuthorizationManager<AuthorizationContext> {

  public static final String AUTH_CHECK_NAMESPACE = "AUTH_CHECK_CACHE";

  private final Scheduler scheduler = Schedulers.parallel();

  @Override
  public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext context) {
    return mono.publishOn(this.scheduler).map(auth -> doCheck(auth, context));
  }

  private AuthorizationDecision doCheck(Authentication authentication, AuthorizationContext context) {
    //匿名不允许通过
    if (authentication instanceof AnonymousAuthenticationToken) return new AuthorizationDecision(false);
    Set<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    //开发环境下ADMIN直接放行
    if (SpringHelper.getActiveProfile().equals("dev")) {
      if (authorities.contains(AuthoritiesConstants.ADMIN)) return new AuthorizationDecision(true);
    }
    //TODO: 判断是否拥有访问权
    return new AuthorizationDecision(true);
  }
}
