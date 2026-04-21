package com.intweb.sonriapp.controller;

import com.intweb.sonriapp.dto.TokenPasswordDTO;
import com.intweb.sonriapp.dto.UsuarioDTO;
import com.intweb.sonriapp.model.Rol;
import com.intweb.sonriapp.model.TokenPasswordReset;
import com.intweb.sonriapp.model.Usuario;
import com.intweb.sonriapp.repository.RolRepository;
import com.intweb.sonriapp.service.UsuarioService;
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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, Authentication authentication) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<Rol> roles = rolRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", roles);
        String rol = authentication.getAuthorities().stream().findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", "")).orElse("");
        model.addAttribute("rol", rol);
        return "usuarios";
    }

    @GetMapping("/usuarios/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id)
                .map(u -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", u.getId());
                    data.put("dni", u.getDni());
                    data.put("nombre", u.getNombre());
                    data.put("apellido", u.getApellido());
                    data.put("correo", u.getCorreo());
                    data.put("telefono", u.getTelefono());
                    data.put("fechaNacimiento", u.getFechaNacimiento() != null ? u.getFechaNacimiento().toString() : null);
                    data.put("rol", Map.of("id", u.getRol().getId(), "nombre", u.getRol().getNombre()));
                    data.put("activo", u.getActivo());
                    return ResponseEntity.ok(data);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/usuarios/guardar")
    @ResponseBody
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioDTO dto,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        try {
            Usuario u = usuarioService.crear(dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Usuario creado. Se envió un correo a " + u.getCorreo() + " para activar la cuenta."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/usuarios/{id}/editar")
    @ResponseBody
    public ResponseEntity<?> editarUsuario(@PathVariable Integer id,
                                           @Valid @RequestBody UsuarioDTO dto,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        try {
            usuarioService.editar(id, dto);
            return ResponseEntity.ok(Map.of("success", true, "message", "Usuario actualizado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/usuarios/{id}/rol")
    @ResponseBody
    public ResponseEntity<?> cambiarRol(@PathVariable Integer id,
                                        @RequestBody Map<String, Integer> body) {
        try {
            usuarioService.cambiarRol(id, body.get("rolId"));
            return ResponseEntity.ok(Map.of("success", true, "message", "Rol actualizado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/usuarios/{id}/toggle-activo")
    @ResponseBody
    public ResponseEntity<?> toggleActivo(@PathVariable Integer id) {
        try {
            usuarioService.toggleActivo(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Estado del usuario actualizado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/usuarios/{id}/reset-password")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@PathVariable Integer id) {
        try {
            usuarioService.solicitarResetPassword(id);
            return ResponseEntity.ok(Map.of("success", true,
                    "message", "Se envió un correo al usuario para que cambie su contraseña"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/set-password")
    public String paginaSetPassword(@RequestParam String token, Model model) {
        Optional<TokenPasswordReset> tokenObj = usuarioService.validarToken(token);
        if (tokenObj.isEmpty()) {
            model.addAttribute("error", "El enlace no es válido o ya expiró. Contacta al administrador.");
            return "set-password";
        }
        model.addAttribute("token", token);
        model.addAttribute("tipo", tokenObj.get().getTipo().name());
        model.addAttribute("nombre", tokenObj.get().getUsuario().getNombre());
        return "set-password";
    }

    @PostMapping("/set-password")
    @ResponseBody
    public ResponseEntity<?> procesarSetPassword(@Valid @RequestBody TokenPasswordDTO dto,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        try {
            usuarioService.establecerPassword(dto);
            return ResponseEntity.ok(Map.of("success", true,
                    "message", "Contraseña establecida correctamente. Ya puedes iniciar sesión."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}