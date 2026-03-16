package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_response.OrderDetailResponse;
import com.example.barber_server.dto.dto_response.OrderResponse;
import com.example.barber_server.models.Order;
import com.example.barber_server.models.OrderDetail;
import com.example.barber_server.repositories.OrderDetailRepository;
import com.example.barber_server.repositories.OrderRepository;
import com.example.barber_server.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderResponse> getOrdersByShopAndDate(Integer shopId, LocalDate date) {
        List<Order> orders = orderRepository.findByShopIdAndOrderDate(shopId,date);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkBarberConflict(Integer barberId, LocalDate date, LocalTime start, LocalTime end) {
        return orderRepository.isBarberBusy(barberId,date, start,end);
    }

    private OrderResponse convertToResponse(Order order) {
        List<OrderDetailResponse> detailResponses = order.getOrderDetails().stream()
                .map(detail -> new OrderDetailResponse(
                        detail.getId(),
                        detail.getShopServiceDetail().getShopService().getService().getName(),
                        detail.getShopServiceDetail().getServiceDetail().getServiceType(),
                        detail.getShopServiceDetail().getServiceDetail().getServiceDetailImages(),
                        detail.getFinalPrice(),
                        detail.getOriginalPrice()
                )).toList();

        return new OrderResponse(
                order.getId(),
                order.getShop().getId(),
                order.getShop().getName(),
                order.getShop().getLongitude(),
                order.getShop().getLatitude(),
                order.getBarber().getId(),
                order.getBarber().getFirstName()+order.getBarber().getLastName(),
                order.getCustomer().getId(),
                order.getCustomer().getFirstName()+order.getCustomer().getLastName(),
                order.getOrderDate(),
                order.getStartTime(),
                order.getEndTime(),
                detailResponses
        );
    }

    @Override
    public OrderRepository addOrder(Order order) {
        if(order.getEndTime().compareTo(order.getStartTime())){

        }
        return null;
    }

    @Override
    public OrderDetailResponse addOrderDetail(Integer orderId, OrderDetail orderDetail) {
        return null;
    }

}
