package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShopServiceDetailResponse {
    private Integer id;
    private Integer serviceDetailId;
    private String name; // Tên món (Cắt tóc Fade, ...)
    private Float price;
    private Boolean isActive;
}
