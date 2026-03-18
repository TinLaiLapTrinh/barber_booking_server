package com.example.barber_server.controllers;


import com.example.barber_server.dto.dto_request.OrderRequest;
import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.dto.dto_response.OrderResponse;
import com.example.barber_server.models.Service;
import com.example.barber_server.repositories.UserRepository;
import com.example.barber_server.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepository;

    @Operation(summary = "Khách hàng đặt lịch")
    @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> createOrder(
            @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal String username
    ) {
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userEntity = userRepository.findByUsername(username);
        if (userEntity == null) return ResponseEntity.status(404).build();

        if (userEntity.getUserType().equals("CUSTOMER")) {
            orderRequest.setCustomerId(userEntity.getId());
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
            @AuthenticationPrincipal String username,
            @RequestParam Map<String, String> params
    ){
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userEntity = userRepository.findByUsername(username);
        if (userEntity == null) return ResponseEntity.status(404).build();

        LocalDate date;
        try {
            date = params.containsKey("date")
                    ? LocalDate.parse(params.get("date"))
                    : LocalDate.now();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        List<OrderResponse> orders;

        if (userEntity.getUserType().equals("CUSTOMER")) {
            orders = orderService.findByCustomerAndOrderDateOrderByStartTimeAsc(userEntity.getId(), date);
        }
        else if (userEntity.getUserType().equals("BARBER")) {
            orders = orderService.findByBarberAndOrderDateOrderByStartTimeAsc(userEntity.getId(), date);
        }
        else {

            orders = List.of();
        }

        return ResponseEntity.ok(orders);
    }

}
