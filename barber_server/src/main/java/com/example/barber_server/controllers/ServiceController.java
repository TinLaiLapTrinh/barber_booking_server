package com.example.barber_server.controllers;

import com.example.barber_server.models.Service;
import com.example.barber_server.models.User;
import com.example.barber_server.services.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService servieService;
    private final ServiceService serviceService;

    @Operation(summary = "Khởi tạo dịch vụ", description = "Khởi tạo dịch vụ cắt tóc na")
    @PostMapping(value = "/service")
    public ResponseEntity<Service> createService(
            @ModelAttribute Service service
    ){
        // Lý tưởng nhất là hàm addService trả về entity đã lưu (có ID từ DB)
        Service savedService = serviceService.addService(service);

        // Thêm .body() để đính kèm object vào response
        return ResponseEntity.status(HttpStatus.CREATED).body(savedService);
    }

}
