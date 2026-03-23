package com.example.barber_server.services;

import com.example.barber_server.clients.MomoClient;
import com.example.barber_server.config.MomoConfig;
import com.example.barber_server.dto.dto_request.MomoRequest;
import com.example.barber_server.dto.dto_response.MomoResponse;
import com.example.barber_server.models.Order;
import com.example.barber_server.models.enums.OrderStatus;
import com.example.barber_server.models.enums.PaymentStatus;
import com.example.barber_server.repositories.OrderRepository;
import com.example.barber_server.utils.SignatureUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.example.barber_server.utils.SignatureUtils.bytesToHex;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final MomoClient momoClient;
    private final MomoConfig momoConfig;
    private final OrderRepository orderRepository;

    public MomoResponse initiateMomoPayment(Integer orderId, Long amount) {
        // Tạo requestId và orderId giả lập duy nhất cho mỗi lần bấm (dùng timestamp)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String requestId = "MOMO_" + orderId + "_" + timestamp;
        String momoOrderId = orderId + "_" + timestamp;

        String orderInfo = "Thanh toan don hang " + orderId;
        String requestType = "captureWallet";
        String extraData = "";

        // Dùng momoOrderId thay cho orderId cũ trong chuỗi ký
        String rawData = "accessKey=" + momoConfig.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + momoConfig.getIpnUrl() +
                "&orderId=" + momoOrderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + momoConfig.getPartnerCode() +
                "&redirectUrl=" + momoConfig.getRedirectUrl() +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = SignatureUtils.computeHmacSha256(rawData, momoConfig.getSecretKey().trim());

        MomoRequest request = MomoRequest.builder()
                .partnerCode(momoConfig.getPartnerCode())
                .requestId(requestId)
                .amount(amount)
                .orderId(momoOrderId) // Gửi ID duy nhất sang MoMo
                .orderInfo(orderInfo)
                .redirectUrl(momoConfig.getRedirectUrl())
                .ipnUrl(momoConfig.getIpnUrl())
                .extraData(extraData)
                .requestType(requestType)
                .signature(signature)
                .lang("vi")
                .build();

        return momoClient.createPayment(request);
    }

    @Transactional
    public void processMomoCallback(Map<String, Object> params) {
        log.info("==> Nhận Callback từ MoMo: {}", params);

        String partnerCode = String.valueOf(params.get("partnerCode"));
        String orderId = String.valueOf(params.get("orderId"));
        String requestId = String.valueOf(params.get("requestId"));

        String amount = String.valueOf(new java.math.BigDecimal(params.get("amount").toString()).longValue());
        String resultCode = String.valueOf(new java.math.BigDecimal(params.get("resultCode").toString()).intValue());
        String transId = String.valueOf(new java.math.BigDecimal(params.get("transId").toString()).longValue());
        String responseTime = String.valueOf(new java.math.BigDecimal(params.get("responseTime").toString()).longValue());

        String message = String.valueOf(params.get("message"));
        String orderInfo = String.valueOf(params.get("orderInfo"));
        String extraData = params.get("extraData") != null ? params.get("extraData").toString() : "";
        String momoSignature = String.valueOf(params.get("signature"));
        String orderType = String.valueOf(params.get("orderType")); // Ví dụ: momo_wallet
        String payType = String.valueOf(params.get("payType"));


        String rawHash = "accessKey=" + momoConfig.getAccessKey().trim() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&message=" + message +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&orderType=" + orderType +
                "&partnerCode=" + partnerCode +
                "&payType=" + payType +
                "&requestId=" + requestId +
                "&responseTime=" + responseTime +
                "&resultCode=" + resultCode +
                "&transId=" + transId;

        log.info("My RawHash Hex: {}", bytesToHex(rawHash.getBytes(StandardCharsets.UTF_8)));
        log.info("F8BBA842ECF85 {}",  momoConfig.getAccessKey().trim());
        log.info("Độ dài Secret Key: {}", momoConfig.getSecretKey().length());
        String secretKey = momoConfig.getSecretKey().trim();
        String mySignature = SignatureUtils.computeHmacSha256(rawHash, secretKey);
        if (!momoSignature.equals(mySignature)) {
            log.error("CẢNH BÁO: Chữ ký không khớp!");
            log.info("Momo Signature: [{}]", momoSignature);
            log.info("My Signature:   [{}]", mySignature);
            log.info("RawHash: [{}]", rawHash);
            return;
        }

        if ("0".equals(resultCode)) {

            Integer originalOrderId = Integer.parseInt(orderId.split("_")[0]);
            Order order = orderRepository.findFirstById(originalOrderId);
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setTotalPrice(Float.parseFloat(amount));
            orderRepository.save(order);
            log.info("--- THÀNH CÔNG: Đơn hàng #{} đã thanh toán! ---", originalOrderId);
            
        }
    }
}
