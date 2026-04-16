package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDetailResponse {
    private Integer id;
    private String name;
    private String address;
    private String avatar;
    private Float latitude;
    private Float longitude;
    private String background;

    private LocationInfo province;
    private LocationInfo ward;
    private List<ShopServiceResponse> shopServiceResponses;
    private Double rateAvg;
    private Long bookingCount;
    private Boolean isActive;


    @Data
    @AllArgsConstructor
    public static class LocationInfo {
        private String code;
        private String name;
    }
}
