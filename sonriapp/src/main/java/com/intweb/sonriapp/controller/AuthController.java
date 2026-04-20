package com.intweb.sonriapp.controller;

import com.intweb.sonriapp.service.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetailsImpl usuario, Model model) {
        model.addAttribute("nombre", usuario.getCorreo());
        model.addAttribute("rol", usuario.getAuthorities().iterator().next().getAuthority());
        return "dashboard";
    }
}