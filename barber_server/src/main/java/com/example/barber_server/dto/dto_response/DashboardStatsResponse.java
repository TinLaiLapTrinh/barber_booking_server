package com.example.barber_server.dto.dto_response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardStatsResponse {
    private Double totalRevenueCurrentMonth;
    private Double totalRevenueLastMonth;
    private Double revenueGrowthRate; // % tăng trưởng
    private Integer totalOrdersCurrentMonth;
    private List<DailyRevenueDTO> dailyRevenues; // Dữ liệu vẽ biểu đồ
}