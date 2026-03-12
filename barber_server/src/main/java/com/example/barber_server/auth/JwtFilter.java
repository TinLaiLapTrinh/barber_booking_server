package com.example.barber_server.auth;

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
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 1. Chạy cho mọi request, Spring Security sẽ tự biết cái nào permitAll cái nào không
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                String role = jwtService.extractRole(token); // Lấy role từ token

                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 3. Đưa vào SecurityContext để hasRole("ADMIN") có tác dụng
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            System.out.println("--- DEBUG SECURITY ---");
            System.out.println("Username: " + SecurityContextHolder.getContext().getAuthentication().getName());
            System.out.println("Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            System.out.println("----------------------");
        }
        filterChain.doFilter(request, response);
    }

}