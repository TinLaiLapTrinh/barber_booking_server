package com.example.barber_server.services;

import com.example.barber_server.dto.dto_request.VoucherRequest;
import com.example.barber_server.dto.dto_response.MessageResponse;
import com.example.barber_server.dto.dto_response.VoucherResponse;

import java.util.List;
import java.util.Map;

public interface VoucherService {

    MessageResponse createVoucher(VoucherRequest voucher, Integer shopId);

    VoucherResponse getVoucherById(Integer voucherId);

    List<VoucherResponse> findAllVouchersByShopId(Integer shopId);

    List<VoucherResponse> findAllVouchersByShopId_ByCondition(Integer shopId, Map<String, String> condition);

    MessageResponse updateVoucher(Integer voucherId, VoucherRequest voucher);



}
