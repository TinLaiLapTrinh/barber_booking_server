package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_response.BarberResponse;
import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.exception.BusinessException;
import com.example.barber_server.exception.ResourceNotFoundException;
import com.example.barber_server.models.Shop;
import com.example.barber_server.models.ShopBarber;
import com.example.barber_server.models.User;
import com.example.barber_server.repositories.*;
import com.example.barber_server.services.ShopBarberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopBarberServiceImpl implements ShopBarberService {
    private final ShopBarberRepository shopBarberRepository;
    private final RateRepository rateRepository;
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Override
    public List<BarberResponse> getBarbersByShopId(Integer shopId) {
        List<ShopBarber> relations = shopBarberRepository.findByShop_Id(shopId);

        return relations.stream()
                .map(u -> BarberResponse.builder()
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

    @Override
    @Transactional
    public MessageResponse createBarberShop(Integer barberId, Integer shopId) {

        Shop shopEntity = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi nhánh Shop với ID: " + shopId));

        User barberEntity = userRepository.findById(barberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thợ Barber với ID: " + barberId));

        if (!"BARBER".equals(barberEntity.getUserType())) {
            throw new BusinessException("Tài khoản này không phải là thợ cắt tóc (Barber), không thể thêm vào shop!");
        }

        Optional<ShopBarber> existingRelation = shopBarberRepository.findByBarberIdAndShopId(barberId, shopId);

        if (existingRelation.isPresent()) {
            ShopBarber shopBarber = existingRelation.get();

            if (shopBarber.getIsActive() != null && shopBarber.getIsActive()) {
                throw new BusinessException("Thợ này đã tồn tại và đang làm việc tại chi nhánh này rồi ní ơi!");
            } else {

                shopBarber.setIsActive(true);
                shopBarberRepository.save(shopBarber);
                return new MessageResponse("Kích hoạt lại trạng thái làm việc thành công cho thợ tại chi nhánh!",barberId);
            }
        }

        ShopBarber newShopBarber =new  ShopBarber();
        newShopBarber.setShop(shopEntity);
        newShopBarber.setBarber(barberEntity);
        newShopBarber.setIsActive(Boolean.TRUE);

        shopBarberRepository.save(newShopBarber);

        return new MessageResponse("Thêm thợ " + barberEntity.getLastName() + " vào chi nhánh " + shopEntity.getName() + " thành công!", barberId);

    }
}
