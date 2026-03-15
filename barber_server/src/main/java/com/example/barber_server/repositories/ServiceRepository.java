package com.example.barber_server.repositories;

import com.example.barber_server.models.Service;
import com.example.barber_server.models.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer>, JpaSpecificationExecutor<Service> {

}
