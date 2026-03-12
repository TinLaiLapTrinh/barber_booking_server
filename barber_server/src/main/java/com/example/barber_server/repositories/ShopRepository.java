package com.example.barber_server.repositories;


import com.example.barber_server.models.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer>, JpaSpecificationExecutor<Shop> {

    Page<Shop> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Shop> findAllByProvinceCode_Code(String provinceCode, Pageable pageable);

    Page<Shop> findAllByWardCode_Code(String wardCode, Pageable pageable);

    Page<Shop> findAllByProvinceCode_AdministrativeUnit_Id(Integer unitId, Pageable pageable);

    Page<Shop> findAllByProvinceCode_FullNameContainingIgnoreCase(String fullName, Pageable pageable);


}
