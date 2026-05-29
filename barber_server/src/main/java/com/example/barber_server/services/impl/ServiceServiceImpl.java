package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_response.ServiceResponse;
import com.example.barber_server.repositories.ServiceRepository;
import com.example.barber_server.services.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
    public final ServiceRepository serviceRepository;


    @Override
    public List<ServiceResponse> findAllService() {
        List<com.example.barber_server.models.Service> services = serviceRepository.findAll();

        List<ServiceResponse> serviceResponses = services.stream()
                .map(this::mapToResponse)
                .toList();

        return serviceResponses;
    }
    private ServiceResponse mapToResponse(com.example.barber_server.models.Service service) {
        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getIcon()
        );
    }

    @Override
    public com.example.barber_server.models.Service addService(com.example.barber_server.models.Service service) {
        if (serviceRepository.existsByName(service.getName())) {
            throw new RuntimeException("Lỗi: Tên dịch vụ '" + service.getName() + "' đã tồn tại trong hệ thống!");
        }
        return serviceRepository.save(service);
    }
}
