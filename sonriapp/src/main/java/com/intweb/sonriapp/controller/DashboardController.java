package com.intweb.sonriapp.controller;

import com.intweb.sonriapp.model.DashboardStats;
import com.intweb.sonriapp.service.DashboardService;
import com.intweb.sonriapp.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetailsImpl usuario, Model model) {
        DashboardStats stats = dashboardService.obtenerEstadisticas();
        boolean esAdmin = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Administrador"));

        model.addAttribute("stats", stats);
        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("citasHoy", dashboardService.obtenerCitasDeHoy());
        model.addAttribute("nombreUsuario", usuario.getUsername());
        model.addAttribute("rol", usuario.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        model.addAttribute("fechaActual", java.time.LocalDate.now());
        model.addAttribute("locale", java.util.Locale.forLanguageTag("es"));
        return "dashboard";
    }
}