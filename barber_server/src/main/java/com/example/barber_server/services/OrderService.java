package com.example.barber_server.services;

import com.example.barber_server.dto.dto_response.OrderDetailResponse;
import com.example.barber_server.dto.dto_response.OrderResponse;
import com.example.barber_server.models.Order;
import com.example.barber_server.models.OrderDetail;
import com.example.barber_server.repositories.OrderRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface OrderService {
    List<OrderResponse> getOrdersByShopAndDate(Integer shopId, LocalDate date);

    boolean checkBarberConflict(Integer barberId, LocalDate date, LocalTime start, LocalTime end);

    OrderRepository addOrder(Order order);

    OrderDetailResponse addOrderDetail(Integer orderId, OrderDetail orderDetail);
}
