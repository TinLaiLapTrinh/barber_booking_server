package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_request.ShopDTO;
import com.example.barber_server.models.Shop;
import com.example.barber_server.repositories.ProvinceRepository;
import com.example.barber_server.repositories.ShopRepository;
import com.example.barber_server.repositories.WardRepository;
import com.example.barber_server.services.ShopService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;

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
    public Page<Shop> filterShops(String name, String provinceCode, Integer unitId, String wardCode, Pageable pageable) {

        if (name != null && provinceCode == null && unitId == null && wardCode == null) {
            return shopRepository.findAllByNameContainingIgnoreCase(name, pageable);
        }

        if (provinceCode != null && name == null && unitId == null && wardCode == null) {
            return shopRepository.findAllByProvinceCode_Code(provinceCode, pageable);
        }

        if (wardCode != null && name == null && provinceCode == null && unitId == null) {
            return shopRepository.findAllByWardCode_Code(wardCode, pageable);
        }

        if (unitId != null && name == null && provinceCode == null && wardCode == null) {
            return shopRepository.findAllByProvinceCode_AdministrativeUnit_Id(unitId, pageable);
        }

        return shopRepository.findAll(createSpecification(name, provinceCode, unitId, wardCode), pageable);
    }

    private Specification<Shop> createSpecification(String name, String provinceCode, Integer unitId, String wardCode) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (provinceCode != null) {
                predicates.add(cb.equal(root.get("provinceCode").get("code"), provinceCode));
            }

            if (unitId != null) {
                predicates.add(cb.equal(root.get("provinceCode").get("administrativeUnit").get("id"), unitId));
            }

            if (wardCode != null) {
                predicates.add(cb.equal(root.get("wardCode").get("code"), wardCode));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    @Transactional
    public Shop createShop(ShopDTO shopDto) {
        // 1. Tái sử dụng hàm validate tọa độ
        validateCoordinates(shopDto.getLatitude(), shopDto.getLongitude());

        // 2. Lấy mã tỉnh/xã từ DTO (Giả sử DTO của bạn trả về String cho Code)
        String pCode = shopDto.getProvinceCode();
        String wCode = shopDto.getWardCode();

        // 3. Tái sử dụng hàm validate location (đã check exists trong DB)
        this.validateLocation(pCode, wCode);

        // 4. CHUYỂN ĐỔI DTO -> MODEL (ENTITY)
        Shop shopEntity = new Shop();
        shopEntity.setName(shopDto.getName());
        shopEntity.setAddress(shopDto.getAddress());
        shopEntity.setLatitude(shopDto.getLatitude());
        shopEntity.setLongitude(shopDto.getLongitude());
        shopEntity.setAvatar(shopDto.getAvatar()); // Link ảnh đã upload từ Controller

        // 5. SET QUAN HỆ (Mapping Relationship)
        // Vì validateLocation đã đảm bảo nó tồn tại, ta dùng getReferenceById để tối ưu hiệu năng
        shopEntity.setProvinceCode(provinceRepository.getReferenceById(pCode));
        shopEntity.setWardCode(wardRepository.getReferenceById(wCode));

        // 6. LƯU MODEL
        try {
            return shopRepository.save(shopEntity);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi hệ thống khi tạo cửa hàng: " + e.getMessage());
        }
    }
    @Override
    public Shop updateShop(Integer id, Shop shopDetails) {
        return null;
    }


}


