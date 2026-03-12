package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    // Chỉ trả về thông tin cần thiết của Tỉnh/Xã
    private LocationInfo province;
    private LocationInfo ward;

    @Data
    @AllArgsConstructor
    public static class LocationInfo {
        private String code;
        private String name;
    }
}
