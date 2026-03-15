package com.example.barber_server.services.impl;

import com.example.barber_server.repositories.ServiceRepository;
import com.example.barber_server.services.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
    public final ServiceRepository serviceRepository;


    @Override
    public List<com.example.barber_server.models.Service> findAllService() {
        return serviceRepository.findAll();
    }

    @Override
    public com.example.barber_server.models.Service addService(com.example.barber_server.models.Service service) {
        if (serviceRepository.existsByName(service.getName())) {
            throw new RuntimeException("Lỗi: Tên dịch vụ '" + service.getName() + "' đã tồn tại trong hệ thống!");
        }
        return serviceRepository.save(service);
    }
}
