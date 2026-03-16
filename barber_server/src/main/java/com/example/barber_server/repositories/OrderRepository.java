package com.example.barber_server.repositories;

import com.example.barber_server.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {

    List<Order> findByShopIdAndOrderDate(Integer shopId, LocalDate orderDate);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o " +
            "WHERE o.barber.id = :barberId " +
            "AND o.orderDate = :orderDate " +
            "AND (:startTime < o.endTime AND :endTime > o.startTime)")
    boolean isBarberBusy(Integer barberId, LocalDate orderDate, LocalTime startTime, LocalTime endTime);

    List<Order> findByBarberIdAndOrderDateOrderByStartTimeAsc(Integer barberId, LocalDate orderDate);
}
