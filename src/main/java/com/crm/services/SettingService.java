package com.crm.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class SettingService extends BaseApiService {

    public SettingService(
            RestTemplate restTemplate,
            @Value("${api.laravel.url}") String apiUrl) {
        super(restTemplate, apiUrl);
    }

    public Map<String, Object> getAllSettings() {
        return fetchSingleData("/settings", Map.class);
    }

    public void updateGlobalDiscountRate(Double rate) {
        restTemplate.put(apiUrl + "/settings/global_discount_rate", Map.of("value", rate));
    }

    public Double getGlobalDiscountRate() {
        return (Double) getAllSettings().get("global_discount_rate");
    }
}
