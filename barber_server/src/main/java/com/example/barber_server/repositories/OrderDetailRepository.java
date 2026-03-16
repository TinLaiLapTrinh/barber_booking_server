package com.example.barber_server.repositories;

import com.example.barber_server.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer>, JpaSpecificationExecutor<OrderDetail> {

}
