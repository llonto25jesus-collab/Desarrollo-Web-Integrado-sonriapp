package com.intweb.sonriapp.controller;

import com.intweb.sonriapp.dto.CitaDTO;
import com.intweb.sonriapp.model.Cita;
import com.intweb.sonriapp.model.HorarioConsultorio;
import com.intweb.sonriapp.repository.PacienteRepository;
import com.intweb.sonriapp.repository.ServicioRepository;
import com.intweb.sonriapp.repository.UsuarioRepository;
import com.intweb.sonriapp.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;


    @GetMapping
    public String verCitas(Model model, Authentication authentication) {
        String rol = authentication.getAuthorities()
                .stream().findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");

        model.addAttribute("rol", rol);
        model.addAttribute("citas", citaService.listarTodas());
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("doctores", usuarioRepository.findByRolIdAndActivoTrue(2));
        model.addAttribute("servicios", servicioRepository.findAllByOrderByNombreAsc());
        model.addAttribute("horarios", citaService.listarHorarios());
        model.addAttribute("hoy", LocalDate.now().toString());
        return "citas";
    }


    @GetMapping("/por-fecha")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> citasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<Cita> citas = citaService.listarPorFecha(fecha);
        List<Map<String, Object>> resultado = citas.stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("hora", c.getHora().toString());
            m.put("fecha", c.getFecha().toString());
            m.put("paciente", c.getPaciente().getNombre() + " " + c.getPaciente().getApellido());
            m.put("doctor", c.getDoctor().getNombre() + " " + c.getDoctor().getApellido());
            m.put("servicio", c.getServicio().getNombre());
            m.put("estado", c.getEstado().name());
            m.put("notas", c.getNotas());
            return m;
        }).toList();
        return ResponseEntity.ok(resultado);
    }


    @GetMapping("/horas-ocupadas")
    @ResponseBody
    public ResponseEntity<List<String>> horasOcupadas(
            @RequestParam Integer doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<String> horas = citaService.horasOcupadasPorDoctorYFecha(doctorId, fecha)
                .stream().map(LocalTime::toString).toList();
        return ResponseEntity.ok(horas);
    }


    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerCita(@PathVariable Integer id) {
        return citaService.obtenerPorId(id).map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("pacienteId", c.getPaciente().getId());
            m.put("paciente", c.getPaciente().getNombre() + " " + c.getPaciente().getApellido());
            m.put("pacienteDni", c.getPaciente().getDni());
            m.put("doctorId", c.getDoctor().getId());
            m.put("doctor", c.getDoctor().getNombre() + " " + c.getDoctor().getApellido());
            m.put("servicioId", c.getServicio().getId());
            m.put("servicio", c.getServicio().getNombre());
            m.put("precio", c.getServicio().getPrecio());
            m.put("fecha", c.getFecha().toString());
            m.put("hora", c.getHora().toString());
            m.put("estado", c.getEstado().name());
            m.put("notas", c.getNotas());
            return ResponseEntity.ok(m);
        }).orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarCita(@Valid @RequestBody CitaDTO dto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }
        try {
            Cita cita = citaService.guardar(dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cita registrada exitosamente",
                    "id", cita.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/{id}/editar")
    @ResponseBody
    public ResponseEntity<?> editarCita(@PathVariable Integer id,
                                        @Valid @RequestBody CitaDTO dto,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }
        try {
            Cita cita = citaService.actualizar(id, dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cita actualizada exitosamente",
                    "id", cita.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PatchMapping("/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id,
                                           @RequestBody Map<String, String> body) {
        try {
            Cita.Estado estado = Cita.Estado.valueOf(body.get("estado"));
            citaService.cambiarEstado(id, estado);
            return ResponseEntity.ok(Map.of("success", true, "message", "Estado actualizado"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Estado inválido"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/{id}/eliminar")
    @ResponseBody
    public ResponseEntity<?> eliminarCita(@PathVariable Integer id) {
        try {
            citaService.eliminar(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Cita eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/horarios/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarHorarios(@RequestBody List<Map<String, Object>> horarios) {
        try {
            for (Map<String, Object> h : horarios) {
                Integer dia    = (Integer) h.get("diaSemana");
                Boolean activo = (Boolean) h.get("activo");
                LocalTime ini  = LocalTime.parse((String) h.get("horaInicio"));
                LocalTime fin  = LocalTime.parse((String) h.get("horaFin"));
                citaService.guardarHorario(dia, activo, ini, fin);
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Horarios guardados correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}