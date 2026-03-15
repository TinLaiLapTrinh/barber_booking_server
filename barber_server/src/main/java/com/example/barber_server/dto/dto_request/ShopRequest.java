package com.example.barber_server.dto.dto_request;

import lombok.Data;

@Data
public class ShopRequest {
    private String name;
    private String address;
    private Float longitude;
    private Float latitude;
    private String provinceCode;
    private String wardCode;
    private String avatar;

}
