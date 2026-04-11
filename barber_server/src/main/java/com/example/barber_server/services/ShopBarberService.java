package com.example.barber_server.services;

import com.example.barber_server.dto.dto_response.UserResponse;
import com.example.barber_server.models.User;

import java.util.List;

public interface ShopBarberService {
    List<UserResponse> getBarbersByShopId(Integer shopId);
}
