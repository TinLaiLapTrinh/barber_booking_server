package com.example.barber_server.repositories;

import com.example.barber_server.models.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory,Integer>, JpaSpecificationExecutor<ServiceCategory> {
}
