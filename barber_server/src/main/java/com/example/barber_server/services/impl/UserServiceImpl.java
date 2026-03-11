package com.example.barber_server.services.impl;


import com.example.barber_server.auth.JwtService;
import com.example.barber_server.models.User;
import com.example.barber_server.repositories.UserRepository;
import com.example.barber_server.services.UserService;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    public final UserRepository userRepository;
    public final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, JwtService jwtService,PasswordEncoder passwordEncoder ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null) {
            return  passwordEncoder.matches(password, user.getPassword());
        }
        return false;

    }

    @Override
    public User getUserByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    public User addUser(User u) {

        if (userRepository.existsByUsername(u.getUsername())) {
            throw new RuntimeException("Err: username '" + u.getUsername() + "' was exist");
        }


        if (userRepository.existsByEmail(u.getEmail())) {
            throw new RuntimeException("Err: Email '" + u.getEmail() + "' have been use!");
        }


        if (u.getPassword().length() < 6) {
            throw new RuntimeException("Lỗi: Mật khẩu phải có ít nhất 6 ký tự!");
        }
        String encodedPassword = passwordEncoder.encode(u.getPassword());
        u.setPassword(encodedPassword);


        if (u.getUserType() == null) {
            u.setUserType("Customer");
        }

        return userRepository.save(u);
    }


    @Override
    public Page<User> getBarbers(Map<String, String> params,int page, int size) {
        return null;
    }

    @Override
    public Page<User> getCustomer(Map<String, String> params, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userType"), "Customer"));
            if (params.get("lastName") != null) {
                predicates.add(cb.like(root.get("firstName"), "%" + params.get("lastName") + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }


    @Override
    public User getUserById(int id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
    }
}
