package com.example.barber_server.services;

import com.example.barber_server.dto.dto_response.BarberResponse;
import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.dto.dto_response.UserResponse;
import com.example.barber_server.models.User;

import java.util.List;

public interface ShopBarberService {
    List<BarberResponse> getBarbersByShopId(Integer shopId);

    MessageResponse createBarberShop(Integer barberId, Integer shopId);
}
