package com.example.barber_server.dto.dto_request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "ID khách hàng không được để trống")
    private Integer customerId;

    @NotNull(message = "ID thợ không được để trống")
    private Integer barberId;

    private Integer voucherId;
    @NotNull(message = "ID cửa hàng không được để trống")
    private Integer shopId;

    @NotNull(message = "Ngày đặt không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @NotEmpty(message = "Đơn hàng phải có ít nhất một dịch vụ")
    private List<OrderDetailRequest> details;
}