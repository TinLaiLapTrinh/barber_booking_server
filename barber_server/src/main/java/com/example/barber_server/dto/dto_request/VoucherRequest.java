package com.example.barber_server.dto.dto_request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherRequest {
    private String name;
    private Double discount;
    private Boolean discountType;
    private Double minOrderValue;
    private Double maxDiscountValue;
    private Integer expiry;
    private LocalDate dateStart;
    private LocalDate expiryDate;
    private Integer quantity;
    private Integer shopId;
}