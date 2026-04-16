package com.example.barber_server.services;

import com.example.barber_server.models.Province;
import com.example.barber_server.models.Ward;

import java.util.List;

public interface LocationService {
    List<Province> getProvince();
    List<Ward> getWardByProvinceId(String provinceCode);
}
