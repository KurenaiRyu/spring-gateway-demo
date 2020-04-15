/*
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.natsusai.gateway.security.jwt;

import static io.github.natsusai.gateway.security.jwt.JWTConstants.ANONYMOUS_KEY;
import static io.github.natsusai.gateway.security.jwt.JWTConstants.ANONYMOUS_USERNAME;
import static io.github.natsusai.gateway.security.jwt.JWTConstants.BEARER_PREFIX;

import io.github.natsusai.gateway.security.AuthoritiesConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JWT转换器
 */
@Slf4j
public class JWTServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final TokenProvider tokenProvider;

    public JWTServerAuthenticationConverter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Apply this function to the current WebExchange, an Authentication object
     * is returned when completed.
     *
     * @param exchange
     * @return
     */
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(BEARER_PREFIX)) {
          log.debug("Login as anonymous user");
          return getAnonymousAuthentication();
        }

        log.debug("Find bearerToken: {}", bearerToken);
        String token = bearerToken.substring(BEARER_PREFIX.length());

        return Mono.justOrEmpty(tokenProvider.verifyToken(token))
            .map(jws ->(Authentication)new UsernamePasswordAuthenticationToken(jws.getBody().getSubject(), ""))
            .switchIfEmpty(getAnonymousAuthentication());
    }

    private Mono<Authentication> getAnonymousAuthentication() {
        UserDetails anonymousUser = User.withUsername(ANONYMOUS_USERNAME)
            .authorities(AuthoritiesConstants.ANONYMOUS)
            .password("")
            .build();
        AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken(ANONYMOUS_KEY,
                                                                                       anonymousUser,
                                                                                       anonymousUser.getAuthorities());
        return Mono.just(authentication);
    }
}
