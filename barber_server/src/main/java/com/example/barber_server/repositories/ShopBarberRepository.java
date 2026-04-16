package com.example.barber_server.repositories;

import com.example.barber_server.models.ShopBarber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ShopBarberRepository extends JpaRepository<ShopBarber, Integer>, JpaSpecificationExecutor<ShopBarber> {

    List<ShopBarber> findByShop_Id(Integer shopId);

    List<ShopBarber> findByShop_IdAndIsActiveTrue(Integer shopId);

    Page<ShopBarber> findAllByBarberId(Integer barberId, Pageable pageable);
}
