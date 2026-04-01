package com.example.barber_server.controllers;


import com.example.barber_server.dto.dto_request.ShopRequest;
import com.example.barber_server.dto.dto_request.VoucherRequest;
import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.dto.dto_response.ShopServiceDetailResponse;
import com.example.barber_server.dto.dto_response.ShopServiceResponse;
import com.example.barber_server.dto.dto_response.VoucherResponse;
import com.example.barber_server.models.Shop;
import com.example.barber_server.services.ShopService;
import com.example.barber_server.services.ShopServiceService;
import com.example.barber_server.services.UploadImageService;
import com.example.barber_server.services.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Shop Controller", description = "Quản lý chi nhánh")
public class ShopController {
    private final ShopService shopService;
    private final ShopServiceService shopServiceService;
    private final UploadImageService uploadService;
    private final VoucherService voucherService;

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
    public ResponseEntity<MessageResponse> createShopService(
            @PathVariable Integer shopId,
            @RequestParam Integer serviceId) {

        return ResponseEntity.status(HttpStatus.CREATED).body(shopServiceService.createShopService(shopId, serviceId));
    }

    @Operation(summary = "Hiển thị các dịch vụ của shop", description = "Hiển thị các dịch vụ hiện có của shop")
    @GetMapping("/shop/{shopId}/services")
    public ResponseEntity<List<ShopServiceResponse>> getShopServiceByShopId(@PathVariable Integer shopId) {
        return ResponseEntity.status(HttpStatus.OK).body(shopServiceService.findByShop_id(shopId));
    }

    @Operation(summary = "Hiển thị các chi tiết dịch vụ của shop", description = "Hiển thị các chi tiết dịch vụ hiện cố của shop")
    @GetMapping("shop/{shopId}/shop-service/{serviceId}/service-detail")
    public ResponseEntity<Page<ShopServiceDetailResponse>> getShopServiceDetail(
            @PathVariable Integer shopId,
            @PathVariable Integer serviceId,
            @RequestParam Integer categoryId,
            @org.springframework.data.web.PageableDefault(size = 10, page = 0) org.springframework.data.domain.Pageable pageable
    ){
        return ResponseEntity.status(HttpStatus.OK).body(shopServiceService.findShopServiceDetailByCategoryId(serviceId,categoryId,pageable));
    }

    @Operation(summary = "Danh sách phiếu giảm giá của shop", description = "Danh sách phiếu giảm giá")
    @GetMapping("/shop/{shopId}/vouchers/conditions")
    public ResponseEntity<List<VoucherResponse>> getShopVouchers(
            @PathVariable Integer shopId,
            @RequestParam Map<String, String> conditions
    ){
    List<VoucherResponse> vouchers = voucherService.findAllVouchersByShopId_ByCondition(shopId,conditions);
        return ResponseEntity.ok(vouchers);
    }

    @Operation(summary = "Tạo phiếu giảm giá cho shop", description = "Tạo phiếu giảm giá")
    @PostMapping(value = "/shop/{shopId}/voucher", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createShopVoucher(
            @PathVariable Integer shopId,
            @ModelAttribute VoucherRequest voucherRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(voucherService.createVoucher(voucherRequest, shopId));
    }




}
