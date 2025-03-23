package com.crm.services;

import com.crm.entities.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ClientService extends BaseApiService {

    public ClientService(
            RestTemplate restTemplate,
            @Value("${api.laravel.url}") String apiUrl) {
        super(restTemplate, apiUrl);
    }

    public List<Client> getAllClients() {
        return fetchData("/clients", new ParameterizedTypeReference<List<Client>>() {});
    }

    public Client getClientById(Long id) {
        return fetchSingleData("/clients/" + id, Client.class);
    }

    public Map<String, Object> getClientDisplayFields(Client client) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("Company Name", client.getCompanyName());
        fields.put("Contact", client.getName());
        fields.put("Email", client.getEmail());
        fields.put("Phone", client.getPrimaryNumber());
        fields.put("Address", String.format("%s, %s %s", client.getAddress(), client.getCity(), client.getZipcode()));
        return fields;
    }
}