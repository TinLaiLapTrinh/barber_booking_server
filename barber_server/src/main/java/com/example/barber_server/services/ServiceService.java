package com.example.barber_server.services;

import com.example.barber_server.models.Service;
import org.springframework.data.domain.Page;

import java.lang.reflect.Parameter;
import java.util.List;

public interface ServiceService {
    List<Service> findAllService();
    Service addService(Service service);
}
