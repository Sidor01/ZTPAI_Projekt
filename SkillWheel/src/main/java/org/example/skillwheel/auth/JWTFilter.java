package org.example.skillwheel.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.example.skillwheel.model.Instructor;
import org.example.skillwheel.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final AuthService authService;

    private static final List<String> WHITELIST = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/v3/api-docs/swagger-config",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/api/docs",
            "/api/docs/**"
    );

    public JWTFilter(JWTUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || authHeader.isBlank()) {
                sendError(response, "Authorization header is missing", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (!authHeader.startsWith("Bearer ")) {
                sendError(response, "Invalid authorization header format", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String token = authHeader.substring(7);

            if (token.isBlank()) {
                sendError(response, "Token cannot be empty", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (!jwtUtil.validateToken(token)) {
                sendError(response, "Invalid or expired token", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String username = jwtUtil.extractUsername(token);
            Instructor instructor = authService.getInstructorFromToken(token);

            if (instructor == null) {
                sendError(response, "User not found", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    instructor,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            sendError(response, "Token expired", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException | SignatureException ex) {
            sendError(response, "Invalid token", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception ex) {
            logger.error("Authentication error", ex);
            sendError(response, "Authentication failed", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        response.getWriter().write(
                String.format("{\"error\": \"%s\", \"status\": %d}", message, status)
        );
    }

    private boolean isWhitelisted(String path) {
        return WHITELIST.stream().anyMatch(path::equals) ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui");
    }
}