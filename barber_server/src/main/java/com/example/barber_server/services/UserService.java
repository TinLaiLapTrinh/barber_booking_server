package com.example.barber_server.services;
import com.example.barber_server.dto.dto_response.*;
import com.example.barber_server.models.Shop;
import com.example.barber_server.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;


public interface UserService {
    User getUserByUsername(String username);
    User addUser(User u);
    Boolean authenticate(String username, String password);
    Page<BarberResponse> getBarbers(Map<String, String> params, int page, int size);
    Page<User> getCustomer(Map<String, String> params, int page, int size);
    User getUserById(int id);
    BarberResponse getBarberById(Integer id);
    Page<RateResponse> getRateByBarberId(Integer barberId, Pageable pageable);
    Page<UserResponse> getAllUsers(Pageable pageable);
    MessageResponse updateUser(Integer userId, Boolean isActive);
    Page<ShopResponse> findAllShopResponseByBarberId(Integer barberId, Pageable pageable);
}
