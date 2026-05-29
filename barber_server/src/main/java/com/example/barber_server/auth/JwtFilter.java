    package com.example.barber_server.auth;

    import com.example.barber_server.dto.dto_response.UserPrincipal;
    import com.example.barber_server.repositories.UserRepository;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
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
    @Slf4j
    public class JwtFilter extends OncePerRequestFilter {
        private final JwtService jwtService;
        private final UserRepository userRepository;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            String token = extractToken(request);

            // Xóa bỏ khối if lồng nhau, gộp lại duy nhất 1 nhịp validate
            if (token != null && jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                var userEntity = userRepository.findByUsername(username);
                String role = jwtService.extractRole(token);

                // Xử lý thông minh: Tránh việc bị double chữ "ROLE_ROLE_" gây lỗi 403
                String finalRole = role.toUpperCase().startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();

                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(finalRole));
                UserPrincipal principal = new UserPrincipal(userEntity.getId(), username, authorities);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            // Khối System.out.println DEBUG xịn sò của ní giữ nguyên để theo dõi console
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                System.out.println("--- DEBUG SECURITY ---");
                System.out.println("Username: " + SecurityContextHolder.getContext().getAuthentication().getName());
                System.out.println("Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                System.out.println("----------------------");
            }

            filterChain.doFilter(request, response);
        }

        private String extractToken(HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }

            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("accessToken".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
            return null;
        }

    }