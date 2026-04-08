package com.example.barber_server.services;

import com.example.barber_server.models.User;

import java.util.List;

public interface ShopBarberService {
    List<User> getBarbersByShopId(Integer shopId);
}
