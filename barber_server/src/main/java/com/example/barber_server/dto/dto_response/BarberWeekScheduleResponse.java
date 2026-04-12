package com.example.barber_server.dto.dto_response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class BarberWeekScheduleResponse {
    private Integer orderId;
    private LocalDate orderDate;
    private LocalTime startTime;
    private Integer totalDuration;
    private String status;
}
