package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_request.OrderDetailRequest;
import com.example.barber_server.dto.dto_request.OrderRequest;
import com.example.barber_server.dto.dto_response.ImageResponse;
import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.dto.dto_response.OrderDetailResponse;
import com.example.barber_server.dto.dto_response.OrderResponse;
import com.example.barber_server.exception.BusinessException;
import com.example.barber_server.exception.ResourceNotFoundException;
import com.example.barber_server.models.*;
import com.example.barber_server.models.enums.OrderStatus;
import com.example.barber_server.models.enums.PaymentMethod;
import com.example.barber_server.models.enums.PaymentStatus;
import com.example.barber_server.repositories.*;
import com.example.barber_server.services.OrderService;
import com.example.barber_server.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ShopServiceDetailRepository shopServiceDetailRepository;


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

    @Override
    public Integer createFullOrder(OrderRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));
        User barber = userRepository.findById(request.getBarberId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ"));
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cửa hàng"));

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessException("Giờ kết thúc phải sau giờ bắt đầu!");
        }

        if (this.checkBarberConflict(barber.getId(), request.getOrderDate(), request.getStartTime(), request.getEndTime())) {
            throw new IllegalStateException("Thợ đã bận trong khung giờ này!");
        }
        Order order = new Order();
        order.setCustomer(customer);
        order.setBarber(barber);
        order.setShop(shop);
        order.setOrderDate(request.getOrderDate());
        order.setStartTime(request.getStartTime());
        order.setEndTime(request.getEndTime());
        order.setStatus(OrderStatus.PENDING);
        if (order.getOrderDetails() == null) {
            order.setOrderDetails(new HashSet<>());
        }

        for (OrderDetailRequest dReq : request.getDetails()) {
            ShopServiceDetail ssd = shopServiceDetailRepository.findById(dReq.getShopServiceDetailId())
                    .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setShopServiceDetail(ssd);
            detail.setOriginalPrice(ssd.getPrice());
            detail.setFinalPrice(ssd.getPrice());

            order.getOrderDetails().add(detail);
        }

        Order savedOrder = orderRepository.save(order);

        return savedOrder.getId();
    }

    @Override
    @Transactional
    public OrderResponse addOrder(Order order) {

        if (!order.getEndTime().isAfter(order.getStartTime())) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu!");
        }

        if (this.checkBarberConflict(order.getBarber().getId(), order.getOrderDate(), order.getStartTime(), order.getEndTime())) {
            throw new IllegalStateException("Thợ đã có lịch trong khung giờ này!");
        }

        Order savedOrder = orderRepository.save(order);
        return convertToResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderDetailResponse addOrderDetail(Integer orderId, OrderDetail orderDetail) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch đặt để thêm dịch vụ"));
        orderDetail.setOrder(order);
        OrderDetail orderDetailSaved = orderDetailRepository.save(orderDetail);

        return mapToOrderDetailResponse(orderDetailSaved);
    }

    @Override
    public List<OrderResponse> findByBarberAndOrderDateOrderByStartTimeAsc(Integer barberId, LocalDate orderDate) {
        List<Order> barberOrders = orderRepository.findByBarberIdAndOrderDateOrderByStartTimeAsc(barberId,orderDate);
        return barberOrders.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> findByCustomerAndOrderDateOrderByStartTimeAsc(Integer customerId, LocalDate orderDate) {
        List<Order> barberOrders = orderRepository.findByCustomerIdAndOrderDateOrderByStartTimeAsc(customerId,orderDate);
        return barberOrders.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private OrderDetailResponse mapToOrderDetailResponse(OrderDetail detail) {
        var ssd = detail.getShopServiceDetail();
        var serviceDetail = ssd.getServiceDetail();


        List<ImageResponse> imageResponses = serviceDetail.getServiceDetailImages().stream()
                .map(img -> new ImageResponse(img.getId(), img.getImage()))
                .toList();

        return new OrderDetailResponse(
                detail.getId(),
                ssd.getShopService().getService().getName(),
                serviceDetail.getServiceType(),
                new HashSet<>(imageResponses),
                detail.getFinalPrice(),
                detail.getOriginalPrice()
        );
    }

    private OrderResponse convertToResponse(Order order) {
        List<OrderDetailResponse> detailResponses = order.getOrderDetails().stream()
                .map(this::mapToOrderDetailResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getShop().getId(),
                order.getShop().getName(),
                order.getShop().getLongitude(),
                order.getShop().getLatitude(),
                order.getBarber().getId(),
                order.getBarber().getFirstName() + " " + order.getBarber().getLastName(),
                order.getCustomer().getId(),
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                order.getOrderDate(),
                order.getStartTime(),
                order.getEndTime(),
                detailResponses,
                order.getStatus().name(),
                order.getStatus().getDisplayValue(),
                order.getPaymentStatus().name(),
                order.getPaymentStatus().getDisplayValue(),
                order.getPaymentMethod().getDisplayValue()
        );
    }

    @Override
    public MessageResponse updateOrder(Integer orderId, Map<String, String> params) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng ID: " + orderId));
        if (params.containsKey("status")) {
            String statusStr = params.get("status").toUpperCase();
            try {
                OrderStatus status = OrderStatus.valueOf(statusStr);
                if (order.getStatus() == OrderStatus.COMPLETED) {
                    throw new BusinessException("Đơn hàng đã hoàn thành, không thể đổi trạng thái!");
                }
                else if (order.getStatus() == OrderStatus.CANCELLED) {
                    throw new BusinessException("Đơn hàng đã huỷ, không thể đổi trạng thái!");
                }

                order.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Trạng thái '" + statusStr + "' không hợp lệ!");
            }
        }
        if(params.containsKey("paymentMethod")){
            String paymentMethodStr = params.get("paymentMethod").toUpperCase();
            try{
                PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodStr);
                order.setPaymentMethod(paymentMethod);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Phương thức thanh toán '" + paymentMethodStr + "' không hợp lệ!");
            }
        }
        if(params.containsKey("paymentStatus")){
            if (!SecurityUtils.isAdmin() && !SecurityUtils.isBarber()) {
                throw new AccessDeniedException("Khách hàng không có quyền cập nhật trạng thái thanh toán!");
            }
            String paymentStatusStr = params.get("paymentStatus").toUpperCase();
            try{
                PaymentStatus paymentStatus = PaymentStatus.valueOf(paymentStatusStr);
                order.setPaymentStatus(paymentStatus);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Phương thức thanh toán '" + paymentStatusStr + "' không hợp lệ!");
            }
        }
        orderRepository.save(order);
        return new MessageResponse("Cập nhật đơn hàng thành công!", order.getId());
    }

    @Override
    public MessageResponse cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        SecurityUtils.checkAuthority(order.getCustomer().getId());

        if (!SecurityUtils.isAdmin() && !SecurityUtils.isBarber()) {
            LocalDateTime appointmentTime = LocalDateTime.of(order.getOrderDate(), order.getStartTime());

            if (LocalDateTime.now().isAfter(appointmentTime.minusMinutes(60))) {
                throw new BusinessException("Không thể hủy lịch trước giờ hẹn 60 phút. Vui lòng liên hệ hotline!");
            }
        }

        order.setStatus(OrderStatus.CANCELLED);

//        if (order.getPaymentStatus() == PaymentStatus.PAID && order.getPaymentMethod() != PaymentMethod.CASH) {
//            order.setPaymentStatus(PaymentStatus.REFUNDED);
//        }

        orderRepository.save(order);
        return new MessageResponse("Đã hủy đơn hàng thành công!", order.getId());

    }


}
