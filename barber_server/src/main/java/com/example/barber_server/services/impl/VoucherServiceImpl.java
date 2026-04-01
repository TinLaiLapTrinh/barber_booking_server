package com.example.barber_server.services.impl;

import com.example.barber_server.dto.dto_request.VoucherRequest;
import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.dto.dto_response.VoucherResponse;
import com.example.barber_server.exception.BusinessException;
import com.example.barber_server.models.Shop;
import com.example.barber_server.models.Voucher;
import com.example.barber_server.repositories.ShopRepository;
import com.example.barber_server.repositories.VoucherRepository;
import com.example.barber_server.services.VoucherService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@AllArgsConstructor
@Service
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final ShopRepository shopRepository;

    @Override
    public MessageResponse createVoucher(VoucherRequest voucher, Integer shopId) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy cửa hàng!"));

        if (voucher.getDateStart() == null ) {
            throw new BusinessException("Ngày bắt đầu không được để trống");
        }

        Voucher voucherSave = this.requestConvertToVoucher(voucher, shop);

        this.validateVoucherLogic(voucherSave);
        voucherRepository.save(voucherSave);

        return new MessageResponse("Tạo Voucher thành công cho Shop với ID: " , shop.getId());
    }

    @Override
    public VoucherResponse getVoucherById(Integer voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy Voucher với ID: " + voucherId));
        return voucherConvertToResponse(voucher);
    }

    @Override
    public List<VoucherResponse> findAllVouchersByShopId(Integer shopId) {

        if (shopId == null || !shopRepository.existsById(shopId)) {
            throw new BusinessException("Shop không tồn tại hoặc rỗng");
        }

        return voucherRepository.findByShopIdAndIsActiveTrue(shopId)
                .stream()
                .map(this::voucherConvertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherResponse> findAllVouchersByShopId_ByCondition(Integer shopId, Map<String, String> conditions) {
        double totalPrice = 0.0;
        try {
            if (conditions.containsKey("totalPrice")) {
                totalPrice = Double.parseDouble(conditions.get("totalPrice"));
            }
        } catch (NumberFormatException e) {
            totalPrice = 0.0;
        }

        List<Voucher> vouchers = voucherRepository.findByShopIdAndIsActiveTrue(shopId);

        final Double finalTotalPrice = totalPrice;

        return vouchers.stream()
                .map(v -> {

                    double discountAmount = calculateActualDiscount(v, finalTotalPrice);
                    boolean eligible = finalTotalPrice >= v.getMinOrderValue();

                    VoucherResponse res = this.voucherConvertToResponseUse(v, discountAmount, eligible);

                    res.setActualDiscount(discountAmount);
                    res.setIsEligible(eligible);
                    return res;
                })

                .sorted((v1, v2) -> {

                    int eligibleComp = v2.getIsEligible().compareTo(v1.getIsEligible());
                    if (eligibleComp != 0) return eligibleComp;

                    return v2.getActualDiscount().compareTo(v1.getActualDiscount());
                })
                .collect(Collectors.toList());
    }

    private double calculateActualDiscount(Voucher v, Double totalPrice) {
        if (totalPrice < v.getMinOrderValue()) return 0.0;

        double amount = 0.0;
        if (Boolean.TRUE.equals(v.getDiscountType())) { // Phần trăm
            amount = totalPrice * (v.getDiscount() / 100.0);
            if (v.getMaxDiscountValue() != null && v.getMaxDiscountValue() > 0) {
                amount = Math.min(amount, v.getMaxDiscountValue());
            }
        } else {
            amount = v.getDiscount();
        }
        return amount;
    }

    @Override
    public MessageResponse updateVoucher(Integer voucherId, VoucherRequest voucherRequest) {

        Voucher existingVoucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new BusinessException("Voucher không tồn tại với ID: " + voucherId));

        if (voucherRequest.getName() != null) existingVoucher.setName(voucherRequest.getName());
        if (voucherRequest.getDiscount() != null) existingVoucher.setDiscount(voucherRequest.getDiscount());
        if (voucherRequest.getDiscountType() != null) existingVoucher.setDiscountType(voucherRequest.getDiscountType());
        if (voucherRequest.getMinOrderValue() != null) existingVoucher.setMinOrderValue(voucherRequest.getMinOrderValue());
        if (voucherRequest.getMaxDiscountValue() != null) existingVoucher.setMaxDiscountValue(voucherRequest.getMaxDiscountValue());
        if (voucherRequest.getQuantity() != null) existingVoucher.setQuantity(voucherRequest.getQuantity());

        if (voucherRequest.getExpiry() != null) {
            existingVoucher.setExpiry(voucherRequest.getExpiry());
        }

        if (voucherRequest.getDateStart() != null) {
            existingVoucher.setDateStart(voucherRequest.getDateStart());
        }

        if (voucherRequest.getExpiryDate() != null) {
            existingVoucher.setExpiryDate(voucherRequest.getExpiryDate());
        } else if (voucherRequest.getExpiry() != null || voucherRequest.getDateStart() != null) {
           existingVoucher.setExpiryDate(null);
        }

        this.validateVoucherLogic(existingVoucher);

        voucherRepository.save(existingVoucher);

        return new MessageResponse("Cập nhật Voucher thành công", voucherId);
    }

    private void validateVoucherLogic(Voucher v) {

        if (v.getQuantity() == null || v.getQuantity() < 0) {
            throw new BusinessException("Số lượng không được nhỏ hơn 0");
        }

        if (v.getDiscount() == null || v.getDiscount() <= 0) {
            throw new BusinessException("Giá trị giảm giá phải lớn hơn 0");
        }

        if (v.getExpiry() == null || v.getExpiry() < 0) {
            throw new BusinessException("Hạn (số ngày) không hợp lệ");
        }

        if (v.getExpiryDate() == null) {
            if (v.getDateStart() == null) {
                throw new BusinessException("Cần ngày bắt đầu để tính hạn sử dụng");
            }
            v.setExpiryDate(v.getDateStart().plusDays(v.getExpiry()));
        }

        if (v.getExpiryDate().isBefore(v.getDateStart())) {
            throw new BusinessException("Ngày hết hạn không được trước ngày bắt đầu");
        }

        if (Boolean.TRUE.equals(v.getDiscountType()) && v.getDiscount() > 100) {
            throw new BusinessException("Giảm giá theo phần trăm không được vượt quá 100%");
        }
    }

    private VoucherResponse voucherConvertToResponse(Voucher voucher) {
        return new VoucherResponse(voucher.getId(), voucher.getName(), voucher.getDiscount(),voucher.getExpiry(),
                voucher.getDateStart(),voucher.getExpiryDate(),voucher.getShop().getId(), voucher.getIsActive(), null, null);
    }

    private VoucherResponse voucherConvertToResponseUse(Voucher voucher, Double actualDiscount, Boolean isEligible) {
        return new VoucherResponse(voucher.getId(), voucher.getName(), voucher.getDiscount(),voucher.getExpiry(),
                voucher.getDateStart(),voucher.getExpiryDate(),voucher.getShop().getId(), voucher.getIsActive(), actualDiscount, isEligible);
    }



    private Voucher requestConvertToVoucher(VoucherRequest request, Shop shop) {
        Voucher entity = new Voucher();
        entity.setShop(shop);
        entity.setName(request.getName());
        entity.setDiscount(request.getDiscount());
        entity.setDiscountType(request.getDiscountType());
        entity.setMinOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : 0.0);
        entity.setMaxDiscountValue(request.getMaxDiscountValue());
        entity.setDateStart(request.getDateStart());
        entity.setExpiryDate(request.getExpiryDate());
        entity.setExpiry(request.getExpiry());
        entity.setQuantity(request.getQuantity());
        entity.setIsActive(true);
        return entity;
    }
}
