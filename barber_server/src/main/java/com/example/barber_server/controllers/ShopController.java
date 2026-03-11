package com.example.barber_server.controllers;


import com.example.barber_server.services.ShopService;
import com.example.barber_server.services.UploadImageService;
import com.example.barber_server.services.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class ShopController {
    private final ShopService shopService;
    private final UploadImageService uploadService;

    public ShopController(UserService userService, UploadImageService uploadService, ShopService shopService) {
        this.shopService = shopService;
        this.uploadService= uploadService;
    }
}
