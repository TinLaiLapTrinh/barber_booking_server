package com.example.barber_server.repositories;

import com.example.barber_server.models.ShopServiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShopServiceDetailRepository extends JpaRepository<ShopServiceDetail, Integer>, JpaSpecificationExecutor<ShopServiceDetail> {
}
