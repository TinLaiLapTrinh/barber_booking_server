package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageResponse {
    private Integer id;
    private String url;
}
