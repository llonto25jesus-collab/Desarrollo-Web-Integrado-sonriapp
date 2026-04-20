package com.intweb.sonriapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@Component
public class ReniecApiClient {

    @Value("${reniec.api.url}")
    private String apiUrl;

    @Value("${reniec.api.token}")
    private String token;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ReniecApiClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> consultarDni(String dni) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);
            headers.set("Referer", apiUrl);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl + dni,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return objectMapper.readValue(response.getBody(), Map.class);
            } else {
                throw new RuntimeException("Error al consultar API: " + response.getStatusCode());
            }

        } catch (Exception e) {

            throw new RuntimeException("No se pudo consultar el DNI: " + e.getMessage());
        }
    }
}