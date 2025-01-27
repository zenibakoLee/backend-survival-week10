package kr.megaptera.backendsurvivalweek10.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    private final AccessTokenService accessTokenService;

    public AccessTokenAuthenticationFilter(
            AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = parseAccessToken(request);

        Authentication authentication = accessTokenService
                .authenticate(accessToken);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);


        filterChain.doFilter(request, response);
    }

    /**
     * Http 헤더 분석하여 Authorization value string return
     *
     * @param request
     * @return
     */
    private static String parseAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(i -> i.startsWith(AUTHORIZATION_PREFIX))
                .map(i -> i.substring(AUTHORIZATION_PREFIX.length()))
                .orElse("");
    }
}