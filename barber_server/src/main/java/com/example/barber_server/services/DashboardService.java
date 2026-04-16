package com.example.barber_server.services;

import com.example.barber_server.dto.dto_response.DailyRevenueDTO;
import com.example.barber_server.dto.dto_response.DashboardStatsResponse;
import com.example.barber_server.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final OrderRepository orderRepository;

    public DashboardStatsResponse getAdminStats() {
        LocalDate now = LocalDate.now(); // 2026-04-16
        LocalDate startOfMonth = now.withDayOfMonth(1);

        // 1. Doanh thu tháng hiện tại (Từ 01/04 đến nay)
        Double currentMonthRevenue = orderRepository.getTotalRevenueBetween(startOfMonth, now);
        currentMonthRevenue = (currentMonthRevenue != null) ? currentMonthRevenue : 0.0;

        // 2. Doanh thu tháng trước (Từ 01/03 đến 31/03)
        LocalDate startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDate endOfLastMonth = startOfMonth.minusDays(1);
        Double lastMonthRevenue = orderRepository.getTotalRevenueBetween(startOfLastMonth, endOfLastMonth);
        lastMonthRevenue = (lastMonthRevenue != null) ? lastMonthRevenue : 0.0;

        // 3. Tính % tăng trưởng
        double growthRate = 0.0;
        if (lastMonthRevenue > 0) {
            growthRate = ((currentMonthRevenue - lastMonthRevenue) / lastMonthRevenue) * 100;
        }

        // 4. Lấy dữ liệu biểu đồ từ đầu tháng đến nay
        List<DailyRevenueDTO> chartData = orderRepository.getDailyRevenueStats(startOfMonth, now);

        return DashboardStatsResponse.builder()
                .totalRevenueCurrentMonth(currentMonthRevenue)
                .totalRevenueLastMonth(lastMonthRevenue)
                .revenueGrowthRate(Math.round(growthRate * 100.0) / 100.0)
                .totalOrdersCurrentMonth(orderRepository.getTotalOrdersBetween(startOfMonth, now))
                .dailyRevenues(chartData)
                .build();
    }
}
