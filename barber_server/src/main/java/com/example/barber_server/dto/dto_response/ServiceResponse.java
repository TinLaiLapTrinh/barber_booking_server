package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
    private Integer id;
    private String name;
    private String description;
    private String icon;
}