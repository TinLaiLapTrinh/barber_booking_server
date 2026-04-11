package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_response.UserResponse;
import com.example.barber_server.models.ShopBarber;
import com.example.barber_server.models.User;
import com.example.barber_server.repositories.OrderRepository;
import com.example.barber_server.repositories.RateRepository;
import com.example.barber_server.repositories.ShopBarberRepository;
import com.example.barber_server.services.ShopBarberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopBarberServiceImpl implements ShopBarberService {
    private final ShopBarberRepository shopBarberRepository;
    private final RateRepository rateRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<UserResponse> getBarbersByShopId(Integer shopId) {
        List<ShopBarber> relations = shopBarberRepository.findByShop_Id(shopId);

        return relations.stream()
                .map(u -> UserResponse.builder()
                        .id(u.getBarber().getId())
                        .firstName(u.getBarber().getFirstName())
                        .lastName(u.getBarber().getLastName())
                        .email(u.getBarber().getEmail())
                        .phoneNumber(u.getBarber().getPhoneNumber())
                        .avatar(u.getBarber().getAvatar())
                        .rateAvg(rateRepository.calculateAverageRatingForBarber(u.getBarber().getId()))
                        .bookingCount(orderRepository.countTotalOrdersByBarberId(u.getBarber().getId()))
                        .build())
                .toList();
    }
}
