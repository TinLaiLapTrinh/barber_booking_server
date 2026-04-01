package com.example.barber_server.dto.dto_response;

import com.example.barber_server.models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class OrderResponse {
    private Integer id;
    private Integer shopId;
    private String shopName;
    private Float longitude;
    private Float latitude;
    private Integer barberId;
    private String barberName;
    private Integer userId;
    private String userName;
    private LocalDate orderDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<OrderDetailResponse> orderDetails;
    private String status;
    private String statusName;
    private String paymentStatus;
    private String paymentStatusName;
    private String paymentMethodName;
    private Float totalPrice;
    private Float finalPrice;

}