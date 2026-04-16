package com.example.barber_server.controllers;


import com.example.barber_server.auth.JwtService;
import com.example.barber_server.dto.AuthResponse;
import com.example.barber_server.dto.LoginRequest;
import com.example.barber_server.dto.dto_response.*;
import com.example.barber_server.models.User;
import com.example.barber_server.services.OrderService;
import com.example.barber_server.services.UploadImageService;
import com.example.barber_server.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Quản lý tài khoản và đăng nhập")
public class UserController
{
    private final UserService userService;
    private final JwtService jwtService;
    private final UploadImageService uploadService;
    private final OrderService orderService;

    @Operation(summary = "Đăng nhập hệ thống", description = "Nhận username/password và trả về JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@ModelAttribute LoginRequest request) {
        if (userService.authenticate(request.getUsername(), request.getPassword())) {
            User user = userService.getUserByUsername(request.getUsername());
            String token = jwtService.generateToken(user.getUsername(), user.getUserType());
            return ResponseEntity.ok(new AuthResponse(token));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @Operation(summary = "Lấy danh sách người dùng")
    @GetMapping("")
    public ResponseEntity<?> getUser(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable
    ){
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
        }

        User user = userService.getUserByUsername(principal.getUsername());

        if (user == null || !"ADMIN".equals(user.getUserType())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền truy cập chức năng này");
        }

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Cập nhật trạng thái hoạt động người dùng (ADMIN)")
    @PatchMapping("user/{id}/update-status")
    public ResponseEntity<?> updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Integer id,
            @RequestBody Boolean isActive
    ){
        User currentUser = userService.getUserByUsername(principal.getUsername());

        if (currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Bạn không thể tự vô hiệu hóa tài khoản của chính mình!", id));
        }

        MessageResponse response = userService.updateUser(id, isActive);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lấy thông tin người dùng hiện tại", description = "Dùng Token để lấy Profile")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal != null) {
            User user = userService.getUserByUsername(principal.getUsername());
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User không tồn tại hoặc Token hết hạn");
    }

    @Operation(summary = "Đăng ký khách hàng", description = "Nhận đăng ký khách hàng")
    @PostMapping(value = "/customer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCustomer(
        @ModelAttribute User user,
        @RequestParam(value = "image", required = false) MultipartFile file) {
            try {
                if (file != null && !file.isEmpty()) {

                    String imageUrl = uploadService.uploadImage(file);
                    user.setAvatar(imageUrl);
                }

                user.setUserType("CUSTOMER");
                User savingUser = userService.addUser(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(savingUser);
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Lỗi upload ảnh lên Cloudinary");
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
    }

    @Operation(summary = "Đăng ký thợ cắt tóc", description = "Nhận đăng ký khách hàng")
    @PostMapping(value = "/barber", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBarber(
            @ModelAttribute User user,
            @RequestParam(value = "image", required = false) MultipartFile file) {
                try {
                    if (file != null && !file.isEmpty()) {

                        String imageUrl = uploadService.uploadImage(file);
                        user.setAvatar(imageUrl);
                    }

                    user.setUserType("BARBER");
                    User savedUser = userService.addUser(user);
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
                } catch (IOException e) {
                    return ResponseEntity.status(500).body("Lỗi upload ảnh lên Cloudinary");
                } catch (RuntimeException e) {
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }

//    @PostMapping(value = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> createAdmin(
//            @ModelAttribute User user,
//            @RequestParam(value = "image", required = false) MultipartFile file) {
//        try {
//            if (file != null && !file.isEmpty()) {
//
//                String imageUrl = uploadService.uploadImage(file);
//                user.setAvatar(imageUrl);
//            }
//
//            user.setUserType("Admin");
//            User savedUser = userService.addUser(user);
//            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Lỗi upload ảnh lên Cloudinary");
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @Operation(summary = "Danh sách thợ cắt tóc", description = "Danh sách thợ cắt tóc ('Có phân trang')")
    @GetMapping("/barbers")
    public ResponseEntity<Page<BarberResponse>> getBarbers(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getBarbers(params,page, size));
    }


    @Operation(summary = "Danh sách khách hàng", description = "Danh sách khách hàng ('Có phân trang')")
    @GetMapping("/customers")
    public ResponseEntity<Page<User>> getCustomer(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getCustomer(params,page, size));
    }

    @Operation(summary = "Danh sách đánh giá của barber", description = "Lấy danh sách đánh giá theo ID của thợ cắt tóc (Có phân trang)")
    @GetMapping("/barber/{id}/rates")
    public ResponseEntity<Page<RateResponse>> getRateByBarberId(
            @PathVariable Integer id,
            Pageable pageable
    ) {

        Page<RateResponse> rateResponsePage = userService.getRateByBarberId(id, pageable);
        return ResponseEntity.ok(rateResponsePage);
    }

    @Operation(summary = "Lịch đặt của barber")
    @GetMapping("/barber/{id}/week-schedule")
    public ResponseEntity<List<BarberWeekScheduleResponse>> getBarberSchedule(
            @PathVariable Integer id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(orderService.getBarberScheduleByWeek(id, date));
    }

    @Operation(summary = "Chi tiết barber")
    @GetMapping("/barber/{id}")
    public ResponseEntity<BarberResponse> getBarberById(
            @PathVariable Integer id
    ){
        return ResponseEntity.ok(userService.getBarberById(id));
    }

    @Operation(summary = "Các chi nhánh barber làm việc")
    @GetMapping("/barber/{id}/shop")
    public ResponseEntity<Page<ShopResponse>> getBarberShop(
            @PathVariable Integer id,
            Pageable pageable
    ){
        return ResponseEntity.ok(userService.findAllShopResponseByBarberId(id,pageable));
    }

}
