package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopResponse {
    private Integer id;
    private String name;
    private String address;
    private String avatar;
    private String background;
    private Double rateAvg;
    private Long bookingCount;
    private Boolean isActive;


}
