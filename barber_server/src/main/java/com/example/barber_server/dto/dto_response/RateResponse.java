package com.example.barber_server.dto.dto_response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RateResponse {
    private Integer id;
    private Double rating;
    private String content;
    private String avatar;
    private String fullname;
    private String ordertype;
}
