package com.example.barber_server.dto.dto_request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor // Cần thiết cho Builder
@NoArgsConstructor  // Cần thiết cho Jackson
public class MomoRequest {
    private String partnerCode;
    private String requestId;
    private Long amount;
    private String orderId;
    private String orderInfo;

    @JsonProperty("redirectUrl")
    private String redirectUrl;

    private String ipnUrl;

    @Builder.Default
    private String extraData = ""; // MoMo bắt buộc phải có, dù là chuỗi rỗng

    private String requestType;
    private String signature;
    private String lang;
}