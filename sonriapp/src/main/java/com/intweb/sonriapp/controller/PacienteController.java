package com.intweb.sonriapp.controller;

import com.intweb.sonriapp.dto.PacienteDTO;
import com.intweb.sonriapp.model.Paciente;
import com.intweb.sonriapp.service.PacienteService;
import com.intweb.sonriapp.service.ReniecApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final ReniecApiService reniecApiService;

    @GetMapping
    public String listarPacientes(Model model, Authentication authentication) {
        List<Paciente> pacientes = pacienteService.listarTodos();
        model.addAttribute("pacientes", pacientes);

        String rol = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");
        model.addAttribute("rol", rol);

        return "pacientes";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarPaciente(@RequestBody PacienteDTO pacienteDTO) {
        try {
            Paciente paciente = pacienteService.guardar(pacienteDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Paciente registrado exitosamente");
            response.put("paciente", paciente);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerPaciente(@PathVariable Integer id) {
        return pacienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/editar")
    @ResponseBody
    public ResponseEntity<?> editarPaciente(@PathVariable Integer id,
                                            @RequestBody PacienteDTO pacienteDTO) {
        try {
            Paciente actualizado = pacienteService.actualizar(id, pacienteDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Paciente actualizado exitosamente");
            response.put("paciente", actualizado);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/validar-dni")
    @ResponseBody
    public ResponseEntity<?> validarDni(@RequestParam String dni) {
        try {
            if (pacienteService.existeDni(dni)) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "El DNI ya está registrado en el sistema");
                return ResponseEntity.badRequest().body(response);
            }
            Map<String, String> datos = reniecApiService.consultarDni(dni);
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/verificar-dni")
    @ResponseBody
    public ResponseEntity<?> verificarDni(@RequestParam String dni) {
        boolean existe = pacienteService.existeDni(dni);
        Map<String, Boolean> response = new HashMap<>();
        response.put("existe", existe);
        return ResponseEntity.ok(response);
    }
}