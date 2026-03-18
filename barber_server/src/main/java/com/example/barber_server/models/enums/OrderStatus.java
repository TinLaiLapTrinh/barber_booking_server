package com.example.barber_server.models.enums;


import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy");

    private final String displayValue;

    OrderStatus(String displayValue) {
        this.displayValue = displayValue;
    }
}