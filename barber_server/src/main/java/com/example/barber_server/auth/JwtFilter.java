package com.example.barber_server.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lấy Header Authorization từ request
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String username;

        // 2. Nếu không có Header hoặc Header không bắt đầu bằng "Bearer ", cho đi tiếp
        // (Lớp SecurityConfig sẽ lo việc chặn lại sau nếu API đó yêu cầu bảo mật)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Cắt lấy chuỗi Token (bỏ đi 7 ký tự "Bearer ")
        // ... (phần đầu giữ nguyên)
        token = authHeader.substring(7);
        System.out.println("=== KIỂM TRA JWT ===");
        System.out.println("1. Chuỗi Token: " + token);

        try {
            username = jwtService.extractUsername(token);
            System.out.println("2. Username lấy được: " + username);
        } catch (Exception e) {
            System.out.println(" LỖI GIẢI MÃ TOKEN: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean isValid = jwtService.validateToken(token);
            System.out.println("3. Token có hợp lệ không?: " + isValid);

            if (isValid) {
                System.out.println("4. Đã lưu quyền vào SecurityContextHolder thành công!");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, new ArrayList<>()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } else {
            System.out.println(" Lỗi: Username bị null hoặc Context đã có người đăng nhập");
        }
        // ... (phần sau giữ nguyên)

        // 4. Nếu lấy được username và Context của Spring chưa có ai đăng nhập
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Kiểm tra token xem còn hạn không
            if (jwtService.validateToken(token)) {

                // TẠO CHỨNG MINH THƯ CHO SPRING SECURITY
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        new ArrayList<>() // Chỗ này truyền danh sách quyền (Roles) nếu có
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // BỎ VÀO "KÉT SẮT" ĐỂ SPRING BIẾT LÀ ĐÃ LOGIN THÀNH CÔNG
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Chuyển request sang các bước tiếp theo
        filterChain.doFilter(request, response);
    }
}