package com.crm.services;

import com.crm.entities.Offer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OffersService extends BaseApiService {

    public OffersService(
            RestTemplate restTemplate,
            @Value("${api.laravel.url}") String apiUrl) {
        super(restTemplate, apiUrl);
    }

    public List<Offer> getAllOffers() {
        return fetchData("/offers", new ParameterizedTypeReference<List<Offer>>() {});
    }

    public Offer getOfferById(Long id) {
        return fetchSingleData("/offers/" + id, Offer.class);
    }

    public void updateOffer(Offer offer) {
        restTemplate.put(apiUrl + "/offers/" + offer.getId(), offer);
    }

    public void deleteOffer(Long offerId) {
        restTemplate.delete(apiUrl + "/offers/" + offerId);
    }
}
