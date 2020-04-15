package io.github.natsusai.gateway.config;

import io.github.natsusai.gateway.security.CustomReactiveAuthorizationManager;
import io.github.natsusai.gateway.security.jwt.JWTServerAuthenticationConverter;
import io.github.natsusai.gateway.security.jwt.JWTReactiveAuthenticationManager;
import io.github.natsusai.gateway.security.jwt.JWTServerAuthenticationSuccessHandler;
import io.github.natsusai.gateway.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

/**
 * SpringSecurity配置类
 *
 * @author liufuhong
 * @since 2019-05-27 11:18
 */

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

  /**
   * Security过滤链配置
   **/
  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
      TokenProvider tokenProvider,
      JWTReactiveAuthenticationManager authenticationManager,
      CustomReactiveAuthorizationManager authorizationManager) {

    return http
        // 取消Security默认配置
        .csrf().disable()
        .httpBasic().disable()
        .formLogin().disable()
        .logout().disable()

        //添加jwt拦截器
        .authenticationManager(authenticationManager)
        //允许的未认证能够访问的路径
        .authorizeExchange()
          .pathMatchers("/api/*/v2/api-docs")
          .permitAll()
          .pathMatchers("/api/**")
          .access(authorizationManager)
          .anyExchange()
          .permitAll()
          .and()
        .addFilterAt(jwtAuthenticationWebFilter(tokenProvider, authenticationManager),
            SecurityWebFiltersOrder.AUTHENTICATION)
        .build();
  }

  /**
   * jwt认证过滤器配置
   */
  @Bean
  public AuthenticationWebFilter jwtAuthenticationWebFilter(TokenProvider tokenProvider,
      JWTReactiveAuthenticationManager authenticationManager) {
      ServerAuthenticationConverter jwtConverter = new JWTServerAuthenticationConverter(
          tokenProvider);
      JWTServerAuthenticationSuccessHandler successHandler = new JWTServerAuthenticationSuccessHandler();

      AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(
          authenticationManager);
      authenticationWebFilter.setServerAuthenticationConverter(jwtConverter);
      authenticationWebFilter.setAuthenticationSuccessHandler(
          successHandler);
      authenticationWebFilter
          .setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));

      return authenticationWebFilter;
  }
}
