package com.example.barber_server.controllers;

import com.example.barber_server.dto.dto_response.ImageResponse;
import com.example.barber_server.models.Service;
import com.example.barber_server.models.ServiceDetail;
import com.example.barber_server.models.ServiceDetailImage;
import com.example.barber_server.services.ServiceDetailService;
import com.example.barber_server.services.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService serviceService;
    private final ServiceDetailService serviceDetailService;

    // 1. Tạo nhóm dịch vụ lớn (Ví dụ: Cắt tóc, Gội đầu)
    @Operation(summary = "Khởi tạo dịch vụ")
    @PostMapping(value = "/service")
    public ResponseEntity<Service> createService(@ModelAttribute Service service) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.addService(service));
    }

    // 2. Lấy danh sách nhóm dịch vụ
    @GetMapping("/services")
    public ResponseEntity<List<Service>> getServices() {
        return ResponseEntity.ok(serviceService.findAllService());
    }

    // 3. Tạo chi tiết (Ví dụ: Cắt Fade - Cho Nam)
    @Operation(summary = "Khởi tạo các chi tiết dịch vụ")
    @PostMapping(value = "/service/{serviceId}/detail")
    public ResponseEntity<ServiceDetail> createDetailService(
            @PathVariable Integer serviceId,
            @RequestParam("categoryId") Integer categoryId,
            @ModelAttribute ServiceDetail serviceDetail
    ) {

        ServiceDetail savedDetail = serviceDetailService.addServiceDetail(serviceId, categoryId, serviceDetail);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDetail);
    }


    @Operation(summary = "Upload list hình ảnh cho chi tiết dịch vụ")
    @PostMapping(value = "/detail/{detailId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ImageResponse>> uploadDetailImages(
            @PathVariable Integer detailId,
            @RequestParam("images") List<MultipartFile> files
    ) throws IOException {
        List<ImageResponse> savedImages = serviceDetailService.uploadServiceDetailImages(detailId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedImages);
    }


    @Operation(summary = "Danh sách chi tiết theo nhóm")
    @GetMapping("/service/{serviceId}/details")
    public ResponseEntity<List<ServiceDetail>> getDetailsByService(@PathVariable Integer serviceId) {
        return ResponseEntity.ok(serviceDetailService.findAllByServiceId(serviceId));
    }

}