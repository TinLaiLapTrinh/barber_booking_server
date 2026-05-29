package com.example.barber_server.services.impl;


import com.example.barber_server.auth.JwtService;
import com.example.barber_server.dto.dto_response.*;
import com.example.barber_server.exception.BusinessException;
import com.example.barber_server.models.Rate;
import com.example.barber_server.models.Shop;
import com.example.barber_server.models.ShopBarber;
import com.example.barber_server.models.User;
import com.example.barber_server.repositories.*;
import com.example.barber_server.services.UserService;
import jakarta.persistence.criteria.Predicate;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    public final UserRepository userRepository;
    public final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RateRepository rateRepository;
    private final OrderRepository orderRepository;
    private final ShopBarberRepository shopBarberRepository;
    private final ShopRepository shopRepository;


    @Override
    public Boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null&&user.getIsActive().equals(Boolean.TRUE)) {
            return  passwordEncoder.matches(password, user.getPassword());
        }
        return false;

    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserResponse getUserById(Integer id) {
        User profile = userRepository.findUserById(id);
        return UserResponse.builder()
                .id(profile.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .userType(profile.getUserType())
                .phoneNumber(profile.getPhoneNumber())
                .avatar(profile.getAvatar())
                .isActive(profile.getIsActive())
                .build();
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
            u.setUserType("CUSTOMER");
        }

        return userRepository.save(u);
    }

    @Override
    public MessageResponse updateUserPassword(Integer userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        if (password.length() < 6) {
            throw new RuntimeException("Lỗi: Mật khẩu phải có ít nhất 6 ký tự!");
        }

        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return new MessageResponse("Cập nhật mật khẩu mới thành công cho người dùng: ",userId);
    }


    @Override
    public Page<BarberResponse> getBarbers(Map<String, String> params, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> barbers = userRepository.findAllByUserType("BARBER", pageable);

        return barbers.map(u -> {

            Double avgRating = rateRepository.calculateAverageRatingForBarber(u.getId());
            Long bookingCount = orderRepository.countTotalOrdersByBarberId(u.getId());

            return BarberResponse.builder()
                    .id(u.getId())
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .email(u.getEmail())
                    .phoneNumber(u.getPhoneNumber())
                    .avatar(u.getAvatar())
                    .rateAvg(avgRating != null ? avgRating : 0.0)
                    .bookingCount(bookingCount != null ? bookingCount : 0L)
                    .build();
        });
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

    @Override
    public BarberResponse getBarberById(Integer id) {
        User barber = userRepository.findUserById(id);
        return BarberResponse.builder()
                .id(barber.getId())
                .firstName(barber.getFirstName())
                .lastName(barber.getLastName())
                .email(barber.getEmail())
                .phoneNumber(barber.getPhoneNumber())
                .avatar(barber.getAvatar())
                .rateAvg(rateRepository.calculateAverageRatingForBarber(barber.getId()))
                .bookingCount(orderRepository.countTotalOrdersByBarberId(barber.getId()))
                .build();
    }

    @Override
    public Page<RateResponse> getRateByBarberId(Integer barberId, Pageable pageable) {

        User user = userRepository.findById(barberId).orElse(null);

        if (user == null || !"BARBER".equals(user.getUserType())) {
            throw new BusinessException("ID cung cấp không tồn tại hoặc không phải là Barber");

        }

        Page<Rate> rates = rateRepository.findAllByOrder_Barber_Id(barberId, pageable);

        return rates.map(rate -> RateResponse.builder()
                .id(rate.getId())
                .rating(rate.getRating())
                .content(rate.getContent())
                .avatar(rate.getCustomer() != null ? rate.getCustomer().getAvatar() : null)
                .fullname(rate.getCustomer() != null
                        ? rate.getCustomer().getFirstName() + " " + rate.getCustomer().getLastName()
                        : "Người dùng ẩn danh")
                .ordertype(rate.getOrder() != null
                        ? rate.getOrder().getStatus().name()
                        : "N/A")
                .build());
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        return users.map(user -> UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userType(user.getUserType())
                .phoneNumber(user.getPhoneNumber())
                .avatar(user.getAvatar())
                .isActive(user.getIsActive())
                .build());
    }

    @Override
    public MessageResponse updateUser(Integer userId, Boolean isActive) {
        // Tìm user, nếu không thấy thì có thể ném Exception hoặc trả về thông báo lỗi
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        user.setIsActive(isActive);
        userRepository.save(user);

        // Trả về MessageResponse theo cấu trúc của bạn
        return new MessageResponse("Cập nhật trạng thái người dùng thành công", userId);
    }

    @Override
    public Page<ShopResponse> findAllShopResponseByBarberId(Integer barberId, Pageable pageable) {
        // 1. Lấy danh sách ShopBarber từ database
        Page<ShopBarber> shopBarbers = shopBarberRepository.findAllByBarberId(barberId, pageable);

        // 2. Mapping từ Page<ShopBarber> sang Page<ShopResponse>
        return shopBarbers.map(shopBarber -> {
            // Lấy thực thể Shop từ quan hệ ManyToOne
            Shop shop = shopBarber.getShop();

            // Build DTO từ Entity Shop
            return ShopResponse.builder()
                    .id(shop.getId())
                    .name(shop.getName())
                    .address(shop.getAddress())
                    .avatar(shop.getAvatar())
                    .background(shop.getBackground())
                    // Lưu ý: Các field rateAvg và bookingCount cần có trong Entity Shop
                    // hoặc bạn phải tính toán/fetch thêm từ repository khác
                    .rateAvg(rateRepository.calculateAverageRatingForShop(shop.getId()))
                    .bookingCount(orderRepository.countTotalOrdersByShopId(shop.getId()))
                    .isActive(shop.getIsActive())
                    .build();
        });
    }
}
