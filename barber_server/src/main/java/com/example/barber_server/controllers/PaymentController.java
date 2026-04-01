package com.example.barber_server.controllers;


import com.example.barber_server.dto.dto_response.MomoResponse;
import com.example.barber_server.models.Order;
import com.example.barber_server.repositories.OrderRepository;
import com.example.barber_server.services.OrderService;
import com.example.barber_server.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Operation(summary = "Thanh toán lịch đặt bằng Momo")
    @PostMapping("/momo/{orderId}")
    public ResponseEntity<MomoResponse> createPayment(
            @PathVariable Integer orderId) {

        Order order = orderRepository.findById(orderId).orElse(null);
        Float finalPrice = orderService.getfinalPrice(order);
        log.info("Yêu cầu tạo link MoMo cho đơn hàng: #{} với số tiền: {}", orderId, finalPrice);
        Long amount = Long.parseLong(finalPrice.toString());
        MomoResponse response = paymentService.initiateMomoPayment(orderId,amount);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Thanh toán lịch đặt bằng Momo")
    @PostMapping("/momo-callback")
    public ResponseEntity<Void> handleMomoCallback(@RequestBody Map<String, Object> params) {
        paymentService.processMomoCallback(params);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Thanh toán lịch đặt bằng VNPay")
    @PostMapping("/vnpay/{orderId}")
    public ResponseEntity<Map<String, String>> createVNPayPayment(
            @PathVariable Integer orderId,
            HttpServletRequest request) {
        Order order = orderRepository.findById(orderId).orElse(null);
        Float finalPrice = orderService.getfinalPrice(order);
        long amount = Math.round(finalPrice);

        log.info("Yêu cầu tạo link VNPay cho đơn hàng: #{} với số tiền: {}", orderId, amount);
        String txnRef = orderId + "_" + System.currentTimeMillis();
        String orderInfo = "Thanh toan Barber Shop - Don hang #" + orderId;

        String paymentUrl = paymentService.createPaymentUrl(request, amount, orderInfo, txnRef);

        Map<String, String> response = new HashMap<>();
        response.put("url", paymentUrl);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Nhận phản hồi từ VNPay (IPN)")
    @GetMapping("/vnpay-callback")
    public ResponseEntity<Map<String, String>> handleVNPayCallback(
            @RequestParam Map<String, String> queryParams) {
        Map<String, String> result = paymentService.processVNPayCallback(queryParams);
        return ResponseEntity.ok(result);
    }
}
