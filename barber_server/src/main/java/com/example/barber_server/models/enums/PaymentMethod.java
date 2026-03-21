package com.example.barber_server.models.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CASH("Tiền mặt"),
    MOMO("Ví MoMo"),
    VNPAY("VNPAY"),
    BANK_TRANSFER("Chuyển khoản ngân hàng");

    private final String displayValue;

    PaymentMethod(String displayValue) {
        this.displayValue = displayValue;
    }
}