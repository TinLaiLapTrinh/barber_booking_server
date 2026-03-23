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
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**","/error").permitAll()
                        .requestMatchers("/api/users/login", "/api/users/customers","/api/users/barber").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/services/**","/api/shops/shop").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/orders/order").permitAll()
                                .requestMatchers("/api/payments/momo-callback").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/shops/shop","/api/services/service",
                                "/api/services/service/{serviceId}/detail","/api/services/detail/{detailId}/images").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/orders/order").hasRole("CUSTOMER")
                                .requestMatchers(HttpMethod.DELETE, "/api/orders/order/{id}/cancel").hasAnyRole("CUSTOMER", "BARBER", "ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/api/orders/order/{id}/update").hasAnyRole("BARBER", "ADMIN")
//                        .hasAnyAuthority("ROLE_BARBER", "ROLE_ADMIN")

                        .anyRequest().authenticated()
                ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}