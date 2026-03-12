package com.example.barber_server.repositories;

import com.example.barber_server.models.AdministrativeRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrativeRegionRepository extends JpaRepository<AdministrativeRegion, Integer> {
}
