package com.example.barber_server.services;

import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.dto.dto_response.ShopServiceDetailResponse;
import com.example.barber_server.dto.dto_response.ShopServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopServiceService {

    List<ShopServiceResponse> findByShop_id(Integer shopId);

    Page<ShopServiceDetailResponse> findShopServiceDetailByCategoryId(Integer serviceId, Integer categoryId, Pageable pageable);

    MessageResponse createShopService(Integer shopId, Integer serviceId);
}
