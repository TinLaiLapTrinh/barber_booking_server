package com.example.barber_server.controllers;


import com.example.barber_server.dto.dto_request.ShopDTO;
import com.example.barber_server.models.Shop;
import com.example.barber_server.services.ShopService;
import com.example.barber_server.services.UploadImageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/shops")
public class ShopController {
    private final ShopService shopService;
    private final UploadImageService uploadService;

    public ShopController(UploadImageService uploadService, ShopService shopService) {
        this.shopService = shopService;
        this.uploadService= uploadService;
    }

    @Operation(summary = "Đăng ký tiệm cắt tóc", description = "Đăng ký tiệm cắt tóc")
    @PostMapping(value = "/shop", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createShop(
            @ModelAttribute ShopDTO shopDto,
            @RequestParam(value = "image", required = false) MultipartFile file) throws IOException {

    
        if (file != null && !file.isEmpty()) {
            String imageUrl = uploadService.uploadImage(file);
            System.out.println("--- DEBUG UPLOAD ---");
            System.out.println("Link ảnh từ Cloudinary: " + imageUrl); // Xem link có null không
            shopDto.setAvatar(imageUrl);
        } else {
            System.out.println("--- DEBUG UPLOAD ---");
            System.out.println("Không nhận được file hoặc file trống!");
        }

        // Nếu createShop bắn ra RuntimeException, GlobalExceptionHandler sẽ tự xử lý
        return ResponseEntity.status(HttpStatus.CREATED).body(shopService.createShop(shopDto));
    }


}
