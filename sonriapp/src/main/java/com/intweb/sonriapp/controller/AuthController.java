package com.intweb.sonriapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // LOGIN
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // DASHBOARD GENERAL
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