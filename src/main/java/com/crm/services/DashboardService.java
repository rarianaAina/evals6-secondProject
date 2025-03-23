package com.crm.services;

import com.crm.entities.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final Double globalDiscountRate;

    public DashboardService(
            RestTemplate restTemplate,
            @Value("${api.laravel.url}") String apiUrl,
            @Value("${global.discount.rate}") Double globalDiscountRate) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.globalDiscountRate = globalDiscountRate;
    }

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // Fetch data from Laravel API using ParameterizedTypeReference
        List<Client> clients = fetchData("/clients", new ParameterizedTypeReference<List<Client>>() {});
        List<Project> projects = fetchData("/projects", new ParameterizedTypeReference<List<Project>>() {});
        List<Task> tasks = fetchData("/tasks", new ParameterizedTypeReference<List<Task>>() {});
        List<Invoice> invoices = fetchData("/invoices", new ParameterizedTypeReference<List<Invoice>>() {});
        List<Payment> payments = fetchData("/payments", new ParameterizedTypeReference<List<Payment>>() {});
        List<Offer> offers = fetchData("/offers", new ParameterizedTypeReference<List<Offer>>() {});

        // Store full lists for details view
        data.put("clients", clients);
        data.put("projects", projects);
        data.put("tasks", tasks);
        data.put("invoices", invoices);
        data.put("payments", payments);
        data.put("offers", offers);

        // Calculate totals
        data.put("totalClients", clients.size());
        data.put("totalProjects", projects.size());
        data.put("totalTasks", tasks.size());
        data.put("totalInvoices", invoices.size());
        data.put("totalPayments", payments.size());
        data.put("totalOffers", offers.size());

        // Calculate financial metrics
        BigDecimal totalInvoiceAmount = invoices.stream()
                .flatMap(invoice -> invoice.getInvoiceLines().stream())
                .map(InvoiceLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaymentAmount = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        data.put("totalInvoiceAmount", totalInvoiceAmount);
        data.put("totalPaymentAmount", totalPaymentAmount);
        data.put("outstandingAmount", totalInvoiceAmount.subtract(totalPaymentAmount));

        // Add global discount rate
        data.put("globalDiscountRate", globalDiscountRate);

        return data;
    }

    public Map<String, Object> getDetailedData(String type, Long id) {
        Map<String, Object> data = new HashMap<>();

        switch (type) {
            case "client":
                Client client = fetchSingleData("/clients/" + id, Client.class);
                data.put("client", client);
                data.put("displayFields", getClientDisplayFields(client));
                break;

            case "payment":
                Payment payment = fetchSingleData("/payments/" + id, Payment.class);
                data.put("payment", payment);
                data.put("canModify", true);
                break;

            case "invoice":
                Invoice invoice = fetchSingleData("/invoices/" + id, Invoice.class);
                BigDecimal discountedAmount = calculateDiscountedAmount(invoice);
                data.put("invoice", invoice);
                data.put("discountedAmount", discountedAmount);
                break;

            default:
                return fetchSingleData("/" + type + "s/" + id, Map.class);
        }

        return data;
    }

    public List<?> getListData(String type) {
        switch (type) {
            case "clients":
                return fetchData("/clients", new ParameterizedTypeReference<List<Client>>() {});
            case "payments":
                return fetchData("/payments", new ParameterizedTypeReference<List<Payment>>() {});
            case "invoices":
                return fetchData("/invoices", new ParameterizedTypeReference<List<Invoice>>() {});
            default:
                return null;
        }
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

    private <T> List<T> fetchData(String endpoint, ParameterizedTypeReference<List<T>> responseType) {
        ResponseEntity<List<T>> response = restTemplate.exchange(
                apiUrl + endpoint,
                HttpMethod.GET,
                null,
                responseType
        );
        return response.getBody();
    }

    private <T> T fetchSingleData(String endpoint, Class<T> responseType) {
        return restTemplate.getForObject(apiUrl + endpoint, responseType);
    }

    private Map<String, Object> getClientDisplayFields(Client client) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("Company Name", client.getCompany_name());
        fields.put("Contact", client.getName());
        fields.put("Email", client.getEmail());
        fields.put("Phone", client.getPrimaryNumber());
        fields.put("Address", String.format("%s, %s %s", client.getAddress(), client.getCity(), client.getZipcode()));
        return fields;
    }

    private BigDecimal calculateDiscountedAmount(Invoice invoice) {
        BigDecimal totalAmount = invoice.getInvoiceLines().stream()
                .map(InvoiceLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalAmount.multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(globalDiscountRate)));
    }
}