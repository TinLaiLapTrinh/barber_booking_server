package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_response.ShopServiceResponse;
import com.example.barber_server.models.ServiceDetail;
import com.example.barber_server.models.ShopServiceDetail;
import com.example.barber_server.repositories.*;
import com.example.barber_server.models.ShopService;
import com.example.barber_server.services.ShopServiceSrvice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ShopServiceServiceImpl implements ShopServiceSrvice {
    private final ShopServiceRepository shopServiceRepository;
    private final ShopRepository shopRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceDetailRepository serviceDetailRepository;
    private final ShopServiceDetailRepository shopServiceDetailRepository;

    @Override
    public List<ShopService> findByShop_id(Integer shopId) {
        return shopServiceRepository.findByShop_Id(shopId);
    }

    @Override
    @Transactional
    public ShopServiceResponse createShopService(Integer shopId, Integer serviceId) {
        // 1. Tạo ShopService (Cái cầu nối chính)
        ShopService shopService = new ShopService();
        shopService.setShop(shopRepository.getReferenceById(shopId));
        shopService.setService(serviceRepository.getReferenceById(serviceId));
        ShopService savedShopService = shopServiceRepository.save(shopService);


        List<ServiceDetail> systemDetails = serviceDetailRepository.findServiceDetailByService_Id(serviceId);

        List<ShopServiceDetail> shopDetails = systemDetails.stream().map(sysDetail -> {
            ShopServiceDetail ssd = new ShopServiceDetail();
            ssd.setShopService(savedShopService);
            ssd.setServiceDetail(sysDetail);
            ssd.setPrice(sysDetail.getBasePrice());
            ssd.setIsActive(true);
            ssd.setCreatedAt(Instant.now());
            ssd.setUpdatedAt(Instant.now());
            return ssd;
        }).toList();

        shopServiceDetailRepository.saveAll(shopDetails);

        return new ShopServiceResponse(
                savedShopService.getId(),
                shopId,
                serviceId
        );
    }
}
