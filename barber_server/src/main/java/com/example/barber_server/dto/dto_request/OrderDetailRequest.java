package com.example.barber_server.dto.dto_request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRequest {
    @NotNull(message = "ID dịch vụ không được để trống")
    private Integer shopServiceDetailId;
    private String note;
}