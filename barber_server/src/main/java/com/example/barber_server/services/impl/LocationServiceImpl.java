package com.example.barber_server.services.impl;

import com.example.barber_server.models.Province;
import com.example.barber_server.models.Ward;
import com.example.barber_server.repositories.ProvinceRepository;
import com.example.barber_server.repositories.WardRepository;
import com.example.barber_server.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;
    @Override
    public List<Province> getProvince() {
        return provinceRepository.findAll();
    }

    @Override
    public List<Ward> getWardByProvinceId(String provinceCode) {
        return wardRepository.findAllByProvinceCode_Code(provinceCode);
    }
}
