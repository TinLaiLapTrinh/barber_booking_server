package com.example.barber_server.services;

import com.example.barber_server.dto.dto_request.ShopRequest;
import com.example.barber_server.dto.dto_response.RateResponse;
import com.example.barber_server.dto.dto_response.ShopDetailResponse;
import com.example.barber_server.dto.dto_response.ShopResponse;
import com.example.barber_server.models.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ShopService {
    Page<ShopResponse> filterShops(Map<String, String> params, Pageable pageable);

    ShopResponse createShop (ShopRequest shop, MultipartFile imageFile, MultipartFile backgroundFile);

    Shop updateShop(Integer id, Shop shopDetails);

    Page<RateResponse> getRateByShopId(Integer shopId, Pageable pageable);

    ShopDetailResponse getShopDetail(Integer shopId, Integer categoryId);
}
