package com.example.barber_server.models.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    UNPAID("Chưa thanh toán"),
    PAID("Đã thanh toán"),
    REFUNDED("Đã hoàn tiền"),
    FAILED("Thanh toán thất bại");

    private final String displayValue;

    PaymentStatus(String displayValue) {
        this.displayValue = displayValue;
    }
}