package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_response.ShopServiceResponse;
import com.example.barber_server.repositories.ShopServiceRepository;
import com.example.barber_server.models.ShopService;
import com.example.barber_server.services.ShopServiceSrvice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ShopServiceServiceImpl implements ShopServiceSrvice {
    private final ShopServiceRepository shopServiceRepository;
    @Override
    public List<ShopService> findByShop_id(Integer shopId) {
        return shopServiceRepository.findByShop_Id(shopId);
    }

    @Override
    public ShopServiceResponse createShopService(Integer shopId, Integer serviceId, ShopService shopService) {
        return null;
    }
}
