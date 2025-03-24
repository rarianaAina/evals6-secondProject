package com.crm.services;


import com.crm.entities.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService extends BaseApiService {

    public PaymentService(
            RestTemplate restTemplate,
            @Value("${api.laravel.url}") String apiUrl) {
        super(restTemplate, apiUrl);
    }

    public List<Payment> getAllPayments() {
        return fetchData("/payments", new ParameterizedTypeReference<List<Payment>>() {});
    }

    public Payment getPaymentById(Long id) {
        return fetchSingleData("/payments/" + id, Payment.class);
    }

    public void updatePaymentAmount(Long paymentId, BigDecimal newAmount) {
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setAmount(newAmount);

        restTemplate.put(apiUrl + "/payments/" + paymentId, payment);
    }

    public void deletePayment(Long paymentId) {
        restTemplate.delete(apiUrl + "/payments/" + paymentId);
    }
}
