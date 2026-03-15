package com.example.barber_server.repositories;

import com.example.barber_server.models.ServiceDetailImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceDetailImageRepository extends JpaRepository<ServiceDetailImage,Integer>, JpaSpecificationExecutor<ServiceDetailImage> {
}
