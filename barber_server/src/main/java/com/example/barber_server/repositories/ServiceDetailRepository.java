package com.example.barber_server.repositories;

import com.example.barber_server.models.ServiceDetail;
import org.hibernate.query.criteria.JpaSetJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceDetailRepository extends JpaRepository<ServiceDetail,Integer>, JpaSpecificationExecutor<ServiceDetail> {
    
}
