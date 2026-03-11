package com.example.barber_server.services;
import com.example.barber_server.models.User;
import org.springframework.data.domain.Page;

import java.util.Map;


public interface UserService {
    User getUserByUsername(String username);
    User addUser(User u);
    Boolean authenticate(String username, String password);
    Page<User> getBarbers(Map<String, String> params,int page, int size);
    Page<User> getCustomer(Map<String, String> params, int page, int size);
    User getUserById(int id);
}
