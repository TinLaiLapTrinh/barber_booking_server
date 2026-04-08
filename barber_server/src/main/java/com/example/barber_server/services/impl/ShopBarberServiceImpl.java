package com.example.barber_server.services.impl;

import com.example.barber_server.models.ShopBarber;
import com.example.barber_server.models.User;
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
    @Override
    public List<User> getBarbersByShopId(Integer shopId) {
        List<ShopBarber> relations = shopBarberRepository.findByShop_Id(shopId);

        return relations.stream()
                .map(ShopBarber::getBarber)
                .collect(Collectors.toList());
    }
}
