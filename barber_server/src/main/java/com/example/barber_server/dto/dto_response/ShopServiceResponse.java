package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ShopServiceResponse {
    private Integer id;
    private Integer shopId;
    private Integer serviceId;
    private String serviceName;
    private String serviceDescription;

}
