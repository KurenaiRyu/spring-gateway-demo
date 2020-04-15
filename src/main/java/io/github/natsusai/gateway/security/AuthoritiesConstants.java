package io.github.natsusai.gateway.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final long CACHE_TIME_TO_LIVE_IN_MILLISECONDS = 60 * 1000;

    private AuthoritiesConstants() {
    }
}
