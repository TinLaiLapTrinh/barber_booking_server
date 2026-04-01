package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShopServiceResponse {
    private Integer id;
    private Integer shopId;
    private Integer serviceId;
    private String serviceName;
    private String serviceDescription;

}
