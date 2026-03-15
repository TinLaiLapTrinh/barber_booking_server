package com.example.barber_server.dto.dto_response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDetailResponse {
    private Integer id;
    private String name;
    private Float price;
    private String description;
    private String categoryName;
    private List<String> imageUrls;
}