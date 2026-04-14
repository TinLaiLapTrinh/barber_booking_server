package com.example.barber_server.services;

import com.example.barber_server.dto.dto_response.ServiceResponse;
import com.example.barber_server.models.Service;

import java.util.List;

public interface ServiceService {
    List<ServiceResponse> findAllService();
    Service addService(Service service);
}
