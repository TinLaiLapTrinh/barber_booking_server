package com.example.barber_server.dto.dto_response;

import com.example.barber_server.models.ServiceDetailImage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class OrderDetailResponse {
    private Integer id;
    private String serviceName;
    private String serviceType;
    private Set<ServiceDetailImage> serviceDetailImages;
    private Float finalPrice;
    private Float originalPrice;
}
