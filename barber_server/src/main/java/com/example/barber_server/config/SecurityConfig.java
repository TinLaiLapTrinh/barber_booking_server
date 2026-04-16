package com.example.barber_server.config;

import com.example.barber_server.auth.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 1. PUBLIC: Các endpoint không cần đăng nhập
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/error").permitAll()
                        .requestMatchers("/api/users/login", "/api/users/profile","/api/users/customers").permitAll()
                        .requestMatchers("/api/payments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/services/**", "/api/shops/**","/api/orders/order/**","/api/users/barber/{id}/week-schedule","/api/users/barber/{id}","/api/users/barbers","/api/location/**,","/api/users/barber/{id}/shop").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/customer").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/orders/order").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/order/*/cancel").hasAnyRole("CUSTOMER", "BARBER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/order/*/update").hasAnyRole("BARBER", "ADMIN")

                        // 4. ADMIN ONLY: Quản lý hệ thống, tạo Shop, Service, Voucher
                        .requestMatchers(HttpMethod.POST, "/api/shops/shop/**", "/api/services/**","/api/users/barber","/api/orders","api/users","api/users/user/{id}/update-status","/api/admin/dashboard/**").hasRole("ADMIN")

                        // 5. CÒN LẠI: Tất cả các request khác phải đăng nhập
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}