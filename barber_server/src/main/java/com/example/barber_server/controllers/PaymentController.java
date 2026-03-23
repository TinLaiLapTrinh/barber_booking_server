package com.example.barber_server.controllers;


import com.example.barber_server.dto.dto_response.MomoResponse;
import com.example.barber_server.services.OrderService;
import com.example.barber_server.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @Operation(summary = "Thanh toán lịch đặt bằng Momo")
    @PostMapping("/momo/{orderId}")
    public ResponseEntity<MomoResponse> createPayment(
            @PathVariable Integer orderId) {

        log.info("Yêu cầu tạo link MoMo cho đơn hàng: #{} với số tiền: {}", orderId, orderService.totalPrice(orderId));
        Long amount = Long.parseLong(orderService.totalPrice(orderId).toString());
        MomoResponse response = paymentService.initiateMomoPayment(orderId,amount);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Thanh toán lịch đặt bằng Momo")
    @PostMapping("/momo-callback")
    public ResponseEntity<Void> handleMomoCallback(@RequestBody Map<String, Object> params) {
        paymentService.processMomoCallback(params);

        return ResponseEntity.noContent().build();
    }
}
