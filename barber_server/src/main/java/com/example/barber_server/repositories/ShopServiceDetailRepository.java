package com.example.barber_server.repositories;

import com.example.barber_server.models.ShopServiceDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShopServiceDetailRepository extends JpaRepository<ShopServiceDetail, Integer>, JpaSpecificationExecutor<ShopServiceDetail> {

    Page<ShopServiceDetail> findByShopService_IdAndServiceDetail_Category_Id(
            Integer shopServiceId,
            Integer categoryId,
            Pageable pageable
    );

//    Page<ShopServiceDetail> findByShopService_Shop_IdAndServiceDetail_Category_Id(
//            Integer shopId,
//            Integer categoryId,
//            Pageable pageable
//    );

}
