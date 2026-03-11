package com.example.barber_server.services.impl;

import com.example.barber_server.models.Province;
import com.example.barber_server.models.Shop;
import com.example.barber_server.models.Ward;
import com.example.barber_server.repositories.ShopRepository;
import com.example.barber_server.services.ShopService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

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
    public Shop createShop(Shop shop) {
        return null;
    }

}


