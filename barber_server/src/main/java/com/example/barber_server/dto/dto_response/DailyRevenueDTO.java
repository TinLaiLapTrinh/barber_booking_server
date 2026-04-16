package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyRevenueDTO {
    private String date; // Định dạng "yyyy-MM-dd"
    private Double revenue;
    private Long orderCount;
}
