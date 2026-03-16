package com.example.barber_server.services;

import com.example.barber_server.dto.dto_response.ShopServiceResponse;
import com.example.barber_server.models.Service;
import com.example.barber_server.models.ShopService;
import java.util.List;

public interface ShopServiceSrvice {

    List<ShopService> findByShop_id(Integer shopId);

    ShopServiceResponse createShopService(Integer shopId, Integer serviceId);
}
