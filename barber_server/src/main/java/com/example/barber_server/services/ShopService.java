package com.example.barber_server.services;

import com.example.barber_server.dto.dto_request.ShopDTO;
import com.example.barber_server.models.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShopService {
    Page<Shop> filterShops(String name, String provinceCode, Integer unitId, String wardCode, Pageable pageable);
    Shop createShop (ShopDTO shop);
    Shop updateShop(Integer id, Shop shopDetails);
}
