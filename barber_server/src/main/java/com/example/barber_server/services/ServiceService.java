package com.example.barber_server.services;

import com.example.barber_server.models.Service;

import java.util.List;

public interface ServiceService {
    List<Service> findAllService();
    Service addService(Service service);
}
