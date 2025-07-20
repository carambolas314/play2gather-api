package com.play2gather.iam.infrastructure.adapter.filter;

import com.play2gather.iam.domain.port.out.RefreshTokenRepositoryPort;
import com.play2gather.iam.infrastructure.adapter.outbound.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtUtil;

    @Autowired
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace("Bearer ", "");

        try {
            String email = jwtUtil.getEmailFromAccessToken(token);
            Long id = jwtUtil.getIdFromAccessToken(token);
            List<String> roles = jwtUtil.getRolesFromAccessToken(token);
            String jti = jwtUtil.getTokenIdFromAccessToken(token);

            boolean isRevoked = refreshTokenRepositoryPort.isRevoked(UUID.fromString(jti));
            if (isRevoked) {
                SecurityContextHolder.clearContext(); // Token inválido
                filterChain.doFilter(request, response);
                return;
            }

            // Autenticação válida
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    email,
                    id,
                    roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .collect(Collectors.toList())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
