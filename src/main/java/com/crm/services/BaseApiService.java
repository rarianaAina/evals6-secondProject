package com.crm.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.List;

public abstract class BaseApiService {
    protected final RestTemplate restTemplate;
    protected final String apiUrl;

    protected BaseApiService(RestTemplate restTemplate, String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    protected <T> List<T> fetchData(String endpoint, ParameterizedTypeReference<List<T>> responseType) {
        ResponseEntity<List<T>> response = restTemplate.exchange(
                apiUrl + endpoint,
                HttpMethod.GET,
                null,
                responseType
        );
        return response.getBody();
    }

/*    protected <T> List<T> fetchData(String endpoint, ParameterizedTypeReference<List<T>> responseType) {
        ResponseEntity<List<T>> response = restTemplate.exchange(
                apiUrl + endpoint,
                HttpMethod.GET,
                null,
                responseType
        );
        System.out.println("URL appelée : " + apiUrl + endpoint);
        System.out.println("Réponse API : " + response.getBody());

        return response.getBody();
    }*/


    protected <T> T fetchSingleData(String endpoint, Class<T> responseType) {

        return restTemplate.getForObject(apiUrl + endpoint, responseType);
    }

/*    protected <T> T fetchSingleData(String endpoint, Class<T> responseType) {
        System.out.println("URL appelée : " + apiUrl + endpoint);
        T response = restTemplate.getForObject(apiUrl + endpoint, responseType);
        System.out.println("Réponse API : " + response);

        return response;
    }*/

}
