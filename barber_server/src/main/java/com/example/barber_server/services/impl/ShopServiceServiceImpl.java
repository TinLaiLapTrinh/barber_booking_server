package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.dto.dto_response.ServiceDetailResponse;
import com.example.barber_server.dto.dto_response.ShopServiceDetailResponse;
import com.example.barber_server.dto.dto_response.ShopServiceResponse;
import com.example.barber_server.models.ServiceDetail;
import com.example.barber_server.models.ServiceDetailImage;
import com.example.barber_server.models.ShopServiceDetail;
import com.example.barber_server.repositories.*;
import com.example.barber_server.models.ShopService;
import com.example.barber_server.services.ShopServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopServiceServiceImpl implements ShopServiceService {
    private final ShopServiceRepository shopServiceRepository;
    private final ShopRepository shopRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceDetailRepository serviceDetailRepository;
    private final ShopServiceDetailRepository shopServiceDetailRepository;

    @Override
    public List<ShopServiceResponse> findByShop_id(Integer shopId) {
        List<ShopService> shopServices = shopServiceRepository.findByShop_Id(shopId);

        return shopServices.stream()
                .map(ss -> new ShopServiceResponse(
                        ss.getId(),
                        ss.getShop().getId(),
                        ss.getService().getId(),
                        ss.getService().getName(),
                        ss.getService().getDescription()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ShopServiceDetailResponse> findShopServiceDetailByCategoryId(Integer serviceId, Integer categoryId, Pageable pageable) {
        Page<ShopServiceDetail> entities = shopServiceDetailRepository
                .findByShopService_IdAndServiceDetail_Category_Id(serviceId, categoryId, pageable);

        return entities.map(this::convertToShopServiceDetailResponse);
    }

    private ShopServiceDetailResponse convertToShopServiceDetailResponse(ShopServiceDetail entity) {
        ServiceDetail sd = entity.getServiceDetail();

        List<String> images = sd.getServiceDetailImages().stream()
                .map(ServiceDetailImage::getImage)
                .toList();

        ServiceDetailResponse sdRes = new ServiceDetailResponse(
                sd.getId(),
                sd.getServiceType(),
                sd.getBasePrice(),
                sd.getDescription(),
                sd.getCategory().getName(),
                images
        );

        return new ShopServiceDetailResponse(
                entity.getId(),
                sdRes,
                entity.getPrice(),
                entity.getIsActive()
        );
    }

    @Override
    @Transactional
    public MessageResponse createShopService(Integer shopId, Integer serviceId) {

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

        return new MessageResponse("Tạo thành công dịch vụ ", serviceId);
    }
}
