package com.example.barber_server.repositories;

import com.example.barber_server.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer>, JpaSpecificationExecutor<OrderDetail> {
    List<OrderDetail> findByOrder_Id(Integer orderId);
}
