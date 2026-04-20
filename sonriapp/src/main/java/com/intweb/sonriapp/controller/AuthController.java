package com.intweb.sonriapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // LOGIN
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // REDIRECCIÓN SEGÚN ROL (IMPORTANTE)
    @GetMapping("/redirect")
    public String redirect(Authentication auth) {

        for (GrantedAuthority role : auth.getAuthorities()) {

            if (role.getAuthority().equals("ROLE_ADMIN")) {
                return "redirect:/admin";
            }

            if (role.getAuthority().equals("ROLE_DOCTOR")) {
                return "redirect:/doctor";
            }

            if (role.getAuthority().equals("ROLE_PACIENTE")) {
                return "redirect:/paciente";
            }
        }

        return "redirect:/login";
    }

    // DASHBOARD (opcional)
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // ADMIN
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    // DOCTOR
    @GetMapping("/doctor")
    public String doctor() {
        return "doctor";
    }

    // PACIENTE
    @GetMapping("/paciente")
    public String paciente() {
        return "paciente";
    }
}