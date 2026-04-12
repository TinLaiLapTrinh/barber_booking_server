package com.example.barber_server.services;

import com.example.barber_server.dto.dto_request.OrderRequest;
import com.example.barber_server.dto.dto_request.RateRequest;
import com.example.barber_server.dto.dto_response.BarberWeekScheduleResponse;
import com.example.barber_server.dto.dto_response.ListOrderResponse;
import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.dto.dto_response.OrderResponse;
import com.example.barber_server.models.Order;
import com.example.barber_server.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface OrderService {
    List<OrderResponse> getOrdersByShopAndDate(Integer shopId, LocalDate date);

    boolean checkBarberConflict(Integer barberId, LocalDate date, LocalTime start, LocalTime end);

    Integer createFullOrder(OrderRequest request);

    List<OrderResponse> findByBarberAndOrderDateOrderByStartTimeAsc(Integer barberId, LocalDate orderDate);

    List<OrderResponse> findByCustomerAndOrderDateOrderByStartTimeAsc(Integer customerId,  LocalDate orderDate);

    MessageResponse updateOrder(Integer orderId, Map<String, String> params);

    MessageResponse cancelOrder(Integer orderId);

    Float getFinalPrice(Order order);

    Page<ListOrderResponse> orderHistory(Integer customerId, Pageable pageable);

    MessageResponse rating(Integer orderId, User customer, RateRequest rateRequest);

    List<BarberWeekScheduleResponse> getBarberScheduleByWeek(Integer barberId, LocalDate date);
}
