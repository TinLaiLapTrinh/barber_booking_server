package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListOrderResponse {
    private Integer id;

    private String shopName;
    private String shopAddress;
    private String shopAvatar;

    private LocalDate orderDate;
    private LocalTime startTime;

    private Integer barberId;
    private String barberName;
    private Integer customerId;
    private String customerName;

    private String status;
    private String statusName;

    private Float finalPrice;

    private String serviceSummary;

    private Integer TotalDuration;

}