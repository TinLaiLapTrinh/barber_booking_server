package com.example.barber_server.dto.dto_response;

import lombok.Data;

@Data
public class MomoResponse {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Long amount;
    private String responseTime;
    private String message;
    private Integer resultCode;
    private String payUrl;
    private String qrCodeUrl;
}
