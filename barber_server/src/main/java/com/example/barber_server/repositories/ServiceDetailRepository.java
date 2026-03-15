package com.example.barber_server.repositories;

import com.example.barber_server.models.ServiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ServiceDetailRepository extends JpaRepository<ServiceDetail,Integer>, JpaSpecificationExecutor<ServiceDetail> {
    List<ServiceDetail> findServiceDetailByService_Id(Integer serviceId);

    List<ServiceDetail> findServiceDetailByCategory_Id(Integer categoryId);

}
