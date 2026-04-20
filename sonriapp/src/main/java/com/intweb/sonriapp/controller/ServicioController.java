package com.intweb.sonriapp.controller;

import com.intweb.sonriapp.dto.ServicioDTO;
import com.intweb.sonriapp.model.Servicio;
import com.intweb.sonriapp.service.ServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    public String listarServicios(Model model, Authentication authentication) {
        List<Servicio> servicios = servicioService.listarTodos();
        model.addAttribute("servicios", servicios);

        String rol = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");
        model.addAttribute("rol", rol);

        return "servicios";
    }


    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerServicio(@PathVariable Integer id) {
        return servicioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarServicio(@Valid @RequestBody ServicioDTO dto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String mensaje = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", mensaje));
        }
        try {
            Servicio servicio = servicioService.guardar(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Servicio registrado exitosamente");
            response.put("servicio", servicio);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/{id}/editar")
    @ResponseBody
    public ResponseEntity<?> editarServicio(@PathVariable Integer id,
                                            @Valid @RequestBody ServicioDTO dto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String mensaje = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", mensaje));
        }
        try {
            Servicio actualizado = servicioService.actualizar(id, dto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Servicio actualizado exitosamente");
            response.put("servicio", actualizado);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/eliminar")
    @ResponseBody
    public ResponseEntity<?> eliminarServicio(@PathVariable Integer id) {
        try {
            servicioService.eliminar(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Servicio eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}