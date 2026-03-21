package com.example.barber_server.controllers;


import com.example.barber_server.dto.dto_request.OrderRequest;
import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.dto.dto_response.OrderResponse;
import com.example.barber_server.dto.dto_response.UserPrincipal;
import com.example.barber_server.exception.ResourceNotFoundException;
import com.example.barber_server.models.Order;
import com.example.barber_server.models.Service;
import com.example.barber_server.repositories.OrderRepository;
import com.example.barber_server.repositories.UserRepository;
import com.example.barber_server.services.OrderService;
import com.example.barber_server.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Quản lý đặt lịch")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Operation(summary = "Khách hàng đặt lịch")
    @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> createOrder(
            @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer userId = principal.getId();
        var authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (authorities.contains("ROLE_CUSTOMER")) {
            orderRequest.setCustomerId(userId);
        } else {
            if (orderRequest.getCustomerId() == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Cần ID khách hàng!", null));
            }
        }
        Integer orderId = orderService.createFullOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Thành công!", orderId));
    }

    @Operation(summary = "Danh sách đặt lịch theo ngày")
    @GetMapping(value = "/order")
    public ResponseEntity<List<OrderResponse>> listOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Map<String, String> params
    ){

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        LocalDate date;
        try {
            String dateStr = params.get("date");
            date = (dateStr != null && !dateStr.isEmpty())
                    ? LocalDate.parse(dateStr)
                    : LocalDate.now();
        } catch (Exception e) {
            date = LocalDate.now();
        }

        Integer userId = principal.getId();
        var authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        List<OrderResponse> orders;

        if (authorities.contains("ROLE_CUSTOMER")) {
            orders = orderService.findByCustomerAndOrderDateOrderByStartTimeAsc(userId, date);
        }
        else if (authorities.contains("ROLE_BARBER")) {

            orders = orderService.findByBarberAndOrderDateOrderByStartTimeAsc(userId, date);
        }
        else if (authorities.contains("ROLE_ADMIN")) {
            orders = List.of();
        }
        else {
            orders = List.of();
        }
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Cập nhật đặt lịch")
    @PatchMapping(value = "/order/{id}/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> updateOrder(
            @PathVariable Integer id,
            @RequestBody Map<String, String> params,
            @AuthenticationPrincipal UserPrincipal principal
    ){

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        boolean isAssignedBarber = Objects.equals(principal.getId(), order.getBarber().getId());

        if (!SecurityUtils.isAdmin() && !isAssignedBarber) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Bạn không có quyền xử lý đơn hàng của thợ khác!", null));
        }

        return ResponseEntity.ok(orderService.updateOrder(id, params));
    }

    @Operation(summary = "Huỷ đặt lịch")
    @DeleteMapping(value = "/order/{id}/cancel")
    public ResponseEntity<MessageResponse> cancelOrder(
            @PathVariable Integer id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal principal
    ){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        if (SecurityUtils.isBarber()) {
            SecurityUtils.checkAuthority(order.getBarber().getId());
        } else if (SecurityUtils.isCustomer()) {
            SecurityUtils.checkAuthority(order.getCustomer().getId());
        }

        String reason = (body != null) ? body.get("reason") : "Không có lý do";
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
