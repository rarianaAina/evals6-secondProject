package com.crm.services;

import com.crm.entities.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceService extends BaseApiService {
    private final SettingService settingService;

    public InvoiceService(
            RestTemplate restTemplate,
            @Value("${api.laravel.url}") String apiUrl,
            SettingService settingService) {
        super(restTemplate, apiUrl);
        this.settingService = settingService;
    }

    public List<Invoice> getAllInvoices() {
        return fetchData("/invoices", new ParameterizedTypeReference<List<Invoice>>() {});
    }

    public Invoice getInvoiceById(Long id) {
        return fetchSingleData("/invoices/" + id, Invoice.class);
    }

    public BigDecimal calculateDiscountedAmount(Invoice invoice) {
        BigDecimal totalAmount = invoice.getInvoiceLines().stream()
                .map(InvoiceLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalAmount.multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(settingService.getGlobalDiscountRate())));
    }
}
