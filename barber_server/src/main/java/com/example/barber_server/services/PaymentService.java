package com.example.barber_server.services;

import com.example.barber_server.clients.MomoClient;
import com.example.barber_server.config.MomoConfig;
import com.example.barber_server.config.VNPayConfig;
import com.example.barber_server.dto.dto_request.MomoRequest;
import com.example.barber_server.dto.dto_response.MomoResponse;
import com.example.barber_server.models.Order;
import com.example.barber_server.models.enums.OrderStatus;
import com.example.barber_server.models.enums.PaymentMethod;
import com.example.barber_server.models.enums.PaymentStatus;
import com.example.barber_server.repositories.OrderRepository;
import com.example.barber_server.utils.SignatureUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.example.barber_server.utils.SignatureUtils.bytesToHex;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final MomoClient momoClient;
    private final MomoConfig momoConfig;
    private final OrderRepository orderRepository;
    private final VNPayConfig vnPayConfig;

    public MomoResponse initiateMomoPayment(Integer orderId, Long amount) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String requestId = "MOMO_" + orderId + "_" + timestamp;
        String momoOrderId = orderId + "_" + timestamp;
        String orderInfo = "Thanh toan don hang " + orderId;
        String requestType = "captureWallet";
        String extraData = "";
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
                .orderId(momoOrderId)
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
            order.setPaymentMethod(PaymentMethod.MOMO);
            order.setTotalPrice(Float.parseFloat(amount));
            orderRepository.save(order);
            log.info("--- THÀNH CÔNG: Đơn hàng #{} đã thanh toán! ---", originalOrderId);

        }
    }

    public String createPaymentUrl(HttpServletRequest request, long amount, String orderInfo, String txnRef) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = vnPayConfig.getTmnCode();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", VNPayConfig.getCurrentTime());

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnPayConfig.getVnpPayUrl() + "?" + queryUrl;
    }

    @Transactional
    public Map<String, String> processVNPayCallback(Map<String, String> queryParams) {
        log.info("==> Nhận Callback từ VNPAY: {}", queryParams);
        String vnp_SecureHash = queryParams.get("vnp_SecureHash");
        Map<String, String> fields = new TreeMap<>();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && key.startsWith("vnp_") && !key.equals("vnp_SecureHash") && !key.equals("vnp_SecureHashType")) {
                if (value != null && !value.isEmpty()) {
                    fields.put(key, value);
                }
            }
        }

        StringJoiner hashData = new StringJoiner("&");
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            try {
                // CỰC KỲ QUAN TRỌNG: Phải encode value thì mới khớp với chữ ký VNPAY gửi về
                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString());
                hashData.add(entry.getKey() + "=" + encodedValue);
            } catch (Exception e) {
                log.error("Lỗi encode: ", e);
            }
        }

        String checkSum = VNPayConfig.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        Map<String, String> response = new HashMap<>();

        if (!checkSum.equalsIgnoreCase(vnp_SecureHash)) {
            log.error("CẢNH BÁO: Chữ ký VNPAY không khớp!");
            log.info("Chuỗi hash tự tính: [{}]", hashData.toString());
            response.put("RspCode", "97");
            response.put("Message", "Invalid Checksum");
            return response;
        }

        String responseCode = queryParams.get("vnp_ResponseCode");
        String txnRef = queryParams.get("vnp_TxnRef");

        if ("00".equals(responseCode)) {

            Integer originalOrderId = Integer.parseInt(txnRef.split("_")[0]);

            Order order = orderRepository.findById(originalOrderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

            if (order.getPaymentStatus() == PaymentStatus.PAID) {
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
            } else {
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setPaymentMethod(PaymentMethod.VNPAY);
                orderRepository.save(order);
                log.info("--- THÀNH CÔNG: Đơn hàng VNPAY #{} đã thanh toán! ---", originalOrderId);

                response.put("RspCode", "00");
                response.put("Message", "Confirm Success");
            }
        } else {
            log.error("Thanh toán thất bại, ResponseCode: {}", responseCode);
            response.put("RspCode", "01");
            response.put("Message", "Payment Failed");
        }

        return response;
    }

}
