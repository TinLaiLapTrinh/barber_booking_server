package com.example.barber_server.repositories;

import com.example.barber_server.dto.dto_response.ShopServiceResponse;
import com.example.barber_server.models.Service;
import com.example.barber_server.models.ShopService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ShopServiceRepository extends JpaRepository<ShopService,Integer>, JpaSpecificationExecutor<ShopService> {
    List<ShopService> findByShop_Id(Integer shopId);
}
