package com.example.barber_server.clients;

import com.example.barber_server.dto.dto_request.MomoRequest;
import com.example.barber_server.dto.dto_response.MomoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "momoClient", url = "https://test-payment.momo.vn/v2/gateway/api/create")
public interface MomoClient {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    MomoResponse createPayment(@RequestBody MomoRequest request);
}