package com.example.barber_server.repositories;


import com.example.barber_server.models.Province;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, String>, JpaSpecificationExecutor<Province> {

    List<Province> findAllByAdministrativeUnit_Id(Integer administrativeUnitId);

    Optional<Province> findByName(String name);
}
