package com.example.barber_server.config;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MomoConfig {
    @Value("${MOMO_PARTNER_CODE}")
    private String partnerCode;

    @Value("${MOMO_ACCESS_KEY}")
    private String accessKey;

    @Value("${MOMO_SECRET_KEY}")
    private String secretKey;

    @Value("${MOMO_REDIRECT_URL}")
    private String redirectUrl;

    @Value("${MOMO_IPN_URL}")
    private String ipnUrl;

    @Value("${MOMO_API_URL}")
    private String apiUrl;
}
