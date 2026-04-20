package com.intweb.sonriapp.service;

import com.intweb.sonriapp.util.ReniecApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReniecApiService {

    private final ReniecApiClient reniecApiClient;

    public Map<String, String> consultarDni(String dni) {

        Map<String, Object> response = reniecApiClient.consultarDni(dni);

        Map<String, String> datos = new HashMap<>();

        String nombre = (String) response.getOrDefault("nombres", "");
        String apellidoPaterno = (String) response.getOrDefault("apellidoPaterno", "");
        String apellidoMaterno = (String) response.getOrDefault("apellidoMaterno", "");

        datos.put("nombre", nombre);
        datos.put("apellido", (apellidoPaterno + " " + apellidoMaterno).trim());

        return datos;
    }
}