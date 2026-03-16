package com.example.barber_server.controllers;


import com.example.barber_server.dto.dto_request.ShopRequest;
import com.example.barber_server.dto.dto_response.ShopServiceResponse;
import com.example.barber_server.models.ServiceDetail;
import com.example.barber_server.models.Shop;
import com.example.barber_server.services.ShopService;
import com.example.barber_server.services.ShopServiceSrvice;
import com.example.barber_server.services.UploadImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shops")
public class ShopController {
    private final ShopService shopService;
    private final ShopServiceSrvice shopServiceSrvice;
    private final UploadImageService uploadService;

    @Operation(summary = "Đăng ký tiệm cắt tóc", description = "Đăng ký tiệm cắt tóc")
    @PostMapping(value = "/shop", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Shop> createShop(
            @ModelAttribute ShopRequest shopRequest,
            @RequestParam(value = "image", required = false) MultipartFile file) throws IOException {


        if (file != null && !file.isEmpty()) {
            String imageUrl = uploadService.uploadImage(file);
            System.out.println("--- DEBUG UPLOAD ---");
            System.out.println("Link ảnh từ Cloudinary: " + imageUrl);
            shopRequest.setAvatar(imageUrl);
        } else {
            System.out.println("--- DEBUG UPLOAD ---");
            System.out.println("Không nhận được file hoặc file trống!");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(shopService.createShop(shopRequest));
    }

    @Operation(summary = "Danh sách cửa hàng", description = "Lấy danh sách shop có phân trang và lọc động")
    @GetMapping(value = "")
    public ResponseEntity<Page<Shop>> getShops(
            @RequestParam Map<String, String> params,
            @org.springframework.data.web.PageableDefault(size = 10, page = 0) org.springframework.data.domain.Pageable pageable
    ) {
        Page<Shop> filteredShops = shopService.filterShops(params, pageable);

        return ResponseEntity.ok(filteredShops);
    }


    @Operation(summary = "Gán dịch vụ cho shop", description = "Tạo liên kết giữa một cửa hàng và một dịch vụ hệ thống")
    @PostMapping("/shop/{shopId}/service")
    public ResponseEntity<ShopServiceResponse> createShopService(
            @PathVariable Integer shopId,
            @RequestParam Integer serviceId) {

        ShopServiceResponse response = shopServiceSrvice.createShopService(shopId, serviceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
