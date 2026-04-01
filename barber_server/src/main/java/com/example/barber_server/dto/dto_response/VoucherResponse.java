package com.example.barber_server.dto.dto_response;

import com.example.barber_server.models.Shop;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherResponse {
    private Integer id;
    private String name;
    private Double discount;
    private Integer expiry;
    private LocalDate dateStart;
    private LocalDate expiryDate;
    private Integer shopId;
    private Boolean isActive;
    private Double actualDiscount;
    private Boolean isEligible;

}
