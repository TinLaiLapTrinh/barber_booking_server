package com.example.barber_server.controllers;


import com.example.barber_server.auth.JwtService;
import com.example.barber_server.dto.AuthResponse;
import com.example.barber_server.dto.LoginRequest;
import com.example.barber_server.models.User;
import com.example.barber_server.services.UploadImageService;
import com.example.barber_server.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Quản lý tài khoản và đăng nhập")
public class UserController
{
    private final UserService userService;
    private final JwtService jwtService;
    private final UploadImageService uploadService;

    public UserController(UserService userService, JwtService jwtService, UploadImageService uploadService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.uploadService= uploadService;
    }


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

                user.setUserType("Customer");
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

                    user.setUserType("Barber");
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
    public ResponseEntity<Page<User>> getBarbers(
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



}
