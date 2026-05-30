package com.thinkalike.taskmanager.security;

import com.thinkalike.taskmanager.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter guarantees this filter runs exactly
    // once per request — not multiple times

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // STEP 1 — read the Authorization header
        // every authenticated request must include:
        // Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
        String authHeader = request.getHeader("Authorization");

        // STEP 2 — if no header or doesn't start with "Bearer "
        // skip this filter and let Spring Security handle it
        // (it will return 401 if the endpoint requires auth)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // STEP 3 — extract the token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // STEP 4 — extract email from token
        String email = jwtUtil.extractEmail(token);

        // STEP 5 — if email found and user not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // STEP 6 — load user from database to verify they still exist
            var userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent() &&
                    jwtUtil.validateToken(token, email)) {

                var user = userOptional.get();

                // STEP 7 — create authentication token with user's role
                // this tells Spring Security "this user is authenticated"
                var authToken = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        ))
                        // ROLE_ADMIN or ROLE_MEMBER
                        // Spring Security requires the "ROLE_" prefix
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // STEP 8 — store authentication in SecurityContext
                // this is how Spring knows the current user for this request
                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        }

        // STEP 9 — continue to the next filter / controller
        filterChain.doFilter(request, response);
    }
}