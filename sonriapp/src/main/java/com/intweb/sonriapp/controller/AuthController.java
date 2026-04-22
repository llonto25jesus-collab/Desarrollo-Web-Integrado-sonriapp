package com.intweb.sonriapp.controller;

import com.intweb.sonriapp.service.ReniecApiService;
import com.intweb.sonriapp.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final ReniecApiService reniecApiService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/reniec/consultar")
    @ResponseBody
    public ResponseEntity<?> consultarReniec(@RequestParam String dni) {
        try {
            var datos = reniecApiService.consultarDni(dni);
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "No se pudo consultar el DNI"));
        }

    }
}