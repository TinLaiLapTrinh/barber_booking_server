package com.example.barber_server.repositories;

import com.example.barber_server.models.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface WardRepository extends JpaRepository<Ward, String>, JpaSpecificationExecutor<Ward> {

    List<Ward> findAllByProvinceCode_Code(String provinceCodeCode);

    Optional<Ward> findByName(String name);

    Boolean existsByCodeAndProvinceCode_Code(String wardCode, String provinceCode);
    
}
