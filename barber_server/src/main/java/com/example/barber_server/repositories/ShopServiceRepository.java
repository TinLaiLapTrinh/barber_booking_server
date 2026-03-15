package com.example.barber_server.repositories;

import com.example.barber_server.models.ShopService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShopServiceRepository extends JpaRepository<ShopService,Integer>, JpaSpecificationExecutor<ShopService> {

}
