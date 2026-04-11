package com.example.barber_server.repositories;

import com.example.barber_server.models.Rate;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RateRepository extends JpaRepository<Rate, Integer>, JpaSpecificationExecutor<Rate> {

    Page<Rate> findAllByOrder_Shop_Id(Integer shopId, Pageable pageable);

    Page<Rate> findAllByOrder_Barber_Id(Integer barberId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Rate r WHERE r.order.shop.id = :shopId")
    Double calculateAverageRatingForShop(@Param("shopId") Integer shopId);

    @Query("SELECT AVG(r.rating) FROM Rate r WHERE r.order.barber.id = :barberId")
    Double calculateAverageRatingForBarber(@Param("barberId") Integer barberId);
}
