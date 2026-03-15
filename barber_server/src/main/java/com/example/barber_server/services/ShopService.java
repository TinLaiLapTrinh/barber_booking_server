package com.example.barber_server.services;

import com.example.barber_server.dto.dto_request.ShopRequest;
import com.example.barber_server.models.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ShopService {
    Page<Shop> filterShops(Map<String, String> params, Pageable pageable);
    Shop createShop (ShopRequest shop);
    Shop updateShop(Integer id, Shop shopDetails);

}
