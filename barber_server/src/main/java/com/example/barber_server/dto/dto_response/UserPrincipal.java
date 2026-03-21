package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@AllArgsConstructor
@ToString
public class UserPrincipal {
    private final Integer id;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;
}