package com.example.barber_server.repositories;

import com.example.barber_server.dto.dto_response.DailyRevenueDTO;
import com.example.barber_server.dto.dto_response.OrderResponse;
import com.example.barber_server.models.Order;
import com.example.barber_server.models.enums.PaymentStatus;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            "AND o.status <> com.example.barber_server.models.enums.OrderStatus.CANCELLED " +
            "AND (:startTime < o.endTime AND :calculatedEndTime > o.startTime)")
    boolean isBarberBusy(
            @Param("barberId") Integer barberId,
            @Param("orderDate") LocalDate orderDate,
            @Param("startTime") LocalTime startTime,
            @Param("calculatedEndTime") LocalTime calculatedEndTime
    );

    List<Order> findByBarberIdAndOrderDateOrderByStartTimeAsc(Integer barberId, LocalDate orderDate);

    List<Order> findByCustomerIdAndOrderDateOrderByStartTimeAsc(Integer customerId, LocalDate orderDate);

    Page<Order> findAllByCustomerId(Integer customerId, Pageable pageable);

    Order findFirstById(Integer id);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop.id = :shopId")
    Long countTotalOrdersByShopId(@Param("shopId") Integer shopId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.barber.id = :barberId")
    Long countTotalOrdersByBarberId(@Param("barberId") Integer barberId);

    List<Order> findByBarberIdAndOrderDateBetweenOrderByOrderDateAscStartTimeAsc(
            Integer barberId,
            LocalDate startDate,
            LocalDate endDate
    );

    boolean existsByIdAndPaymentStatus(Integer id, PaymentStatus paymentStatus);
    Page<Order>  findAllByOrderDate(LocalDate date, Pageable pageable);

    @Query("SELECT SUM(o.finalPrice) FROM Order o " +
            "WHERE o.status = 'COMPLETED' AND o.orderDate BETWEEN :start AND :end")
    Double getTotalRevenueBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE o.status = 'COMPLETED' AND o.orderDate BETWEEN :start AND :end")
    Integer getTotalOrdersBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT new com.example.barber_server.dto.dto_response.DailyRevenueDTO(" +
            "CAST(o.orderDate AS string), SUM(o.finalPrice), COUNT(o)) " +
            "FROM Order o " +
            "WHERE o.status = 'COMPLETED' AND o.orderDate BETWEEN :start AND :end " +
            "GROUP BY o.orderDate ORDER BY o.orderDate ASC")
    List<DailyRevenueDTO> getDailyRevenueStats(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
