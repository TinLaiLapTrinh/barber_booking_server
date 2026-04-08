package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_request.ShopRequest;
import com.example.barber_server.dto.dto_response.ShopResponse;
import com.example.barber_server.models.Shop;
import com.example.barber_server.repositories.ProvinceRepository;
import com.example.barber_server.repositories.ShopRepository;
import com.example.barber_server.repositories.WardRepository;
import com.example.barber_server.services.ShopService;
import com.example.barber_server.services.UploadImageService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    private final UploadImageService uploadImageService;

    private void validateLocation(String provinceCode, String wardCode) {
        if (!provinceRepository.existsById(provinceCode)) {
            throw new RuntimeException("Tỉnh không tồn tại!");
        }
        if (!wardRepository.existsByCodeAndProvinceCode_Code(wardCode, provinceCode)) {
            throw new RuntimeException("Xã không thuộc Tỉnh đã chọn!");
        }
    }

    private void validateCoordinates(Float lat, Float lon) {
        if (lat == null || lon == null || lat < 8.0 || lat > 24.0 || lon < 102.0 || lon > 110.0) {
            throw new RuntimeException("Tọa độ không hợp lệ tại Việt Nam.");
        }
    }

    @Override
    @Transactional
    public ShopResponse createShop(ShopRequest shopRequest, MultipartFile imageFile, MultipartFile backgroundFile) {
        String avatarUrl = null;
        String backgroundUrl = null;

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                avatarUrl = uploadImageService.uploadImage(imageFile);
            }
            if (backgroundFile != null && !backgroundFile.isEmpty()) {
                backgroundUrl = uploadImageService.uploadImage(backgroundFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage());
        }

        validateCoordinates(shopRequest.getLatitude(), shopRequest.getLongitude());
        this.validateLocation(shopRequest.getProvinceCode(), shopRequest.getWardCode());

        Shop shopEntity = new Shop();
        shopEntity.setName(shopRequest.getName());
        shopEntity.setAddress(shopRequest.getAddress());
        shopEntity.setLatitude(shopRequest.getLatitude());
        shopEntity.setLongitude(shopRequest.getLongitude());
        shopEntity.setAvatar(avatarUrl);
        shopEntity.setBackground(backgroundUrl);
        shopEntity.setProvinceCode(provinceRepository.getReferenceById(shopRequest.getProvinceCode()));
        shopEntity.setWardCode(wardRepository.getReferenceById(shopRequest.getWardCode()));

        shopRepository.save(shopEntity);

        return ShopResponse.builder()
                .id(shopEntity.getId())
                .name(shopEntity.getName())
                .address(shopEntity.getAddress())
                .avatar(shopEntity.getAvatar())
                .background(shopEntity.getBackground())
                .build();
    }


    @Override
    public Shop updateShop(Integer id, Shop shopDetails) {
        return null;
    }


    @Override
    public Page<ShopResponse> filterShops(Map<String, String> params, Pageable pageable) {

        Page<Shop> shopPage;
        String name = params.get("name");
        String provinceCode = params.get("provinceCode");
        String wardCode = params.get("wardCode");
        Integer unitId = null;
        try {
            if (params.get("unitId") != null) {
                unitId = Integer.parseInt(params.get("unitId"));
            }
        } catch (NumberFormatException e) {
            // Log lỗi hoặc bỏ qua nếu unitId gửi lên không phải là số
        }

        if (provinceCode != null && name == null && unitId == null && wardCode == null) {
            shopPage = shopRepository.findAllByProvinceCode_Code(provinceCode, pageable);
        } else if (wardCode != null && name == null && provinceCode == null && unitId == null) {
            shopPage = shopRepository.findAllByWardCode_Code(wardCode, pageable);
        } else if (unitId != null && name == null && provinceCode == null && wardCode == null) {
            shopPage = shopRepository.findAllByProvinceCode_AdministrativeUnit_Id(unitId, pageable);
        } else {
            shopPage = shopRepository.findAll(createSpecification(name, provinceCode, unitId, wardCode), pageable);
        }


        return shopPage.map(this::convertToResponse);
    }

    private ShopResponse convertToResponse(Shop shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress())
                .avatar(shop.getAvatar())
                .background(shop.getBackground())
                .build();
    }
    private Specification<Shop> createSpecification(String name, String provinceCode, Integer unitId, String wardCode) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo tên (không phân biệt hoa thường)
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            // Lọc theo mã tỉnh/thành
            if (provinceCode != null && !provinceCode.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("provinceCode").get("code"), provinceCode));
            }

            // Lọc theo mã đơn vị hành chính (unitId)
            if (unitId != null) {
                predicates.add(cb.equal(root.get("provinceCode").get("administrativeUnit").get("id"), unitId));
            }

            // Lọc theo mã phường/xã
            if (wardCode != null && !wardCode.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("wardCode").get("code"), wardCode));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }



}


