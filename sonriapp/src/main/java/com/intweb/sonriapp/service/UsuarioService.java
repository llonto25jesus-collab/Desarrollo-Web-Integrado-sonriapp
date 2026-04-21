package com.intweb.sonriapp.service;

import com.intweb.sonriapp.dto.TokenPasswordDTO;
import com.intweb.sonriapp.dto.UsuarioDTO;
import com.intweb.sonriapp.model.Rol;
import com.intweb.sonriapp.model.TokenPasswordReset;
import com.intweb.sonriapp.model.Usuario;
import com.intweb.sonriapp.repository.RolRepository;
import com.intweb.sonriapp.repository.TokenPasswordResetRepository;
import com.intweb.sonriapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TokenPasswordResetRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByNombreAsc();
    }

    public Optional<Usuario> obtenerPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Usuario crear(UsuarioDTO dto) {
        validarDniUnico(dto.getDni(), null);
        validarCorreoUnico(dto.getCorreo(), null);

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario u = new Usuario();
        u.setDni(dto.getDni().trim());
        u.setNombre(dto.getNombre().trim());
        u.setApellido(dto.getApellido().trim());
        u.setCorreo(dto.getCorreo().trim().toLowerCase());
        u.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        u.setFechaNacimiento(dto.getFechaNacimiento());
        u.setRol(rol);
        u.setActivo(false);
        u.setContrasena(passwordEncoder.encode(UUID.randomUUID().toString())); // placeholder

        usuarioRepository.save(u);

        // Generar token de activación (48h)
        String token = generarToken(u, TokenPasswordReset.TipoToken.ACTIVACION, 48);
        emailService.enviarActivacionCuenta(u.getCorreo(), u.getNombre() + " " + u.getApellido(), token);

        return u;
    }

    @Transactional
    public Usuario editar(Integer id, UsuarioDTO dto) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        validarDniUnico(dto.getDni(), id);
        validarCorreoUnico(dto.getCorreo(), id);

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        u.setDni(dto.getDni().trim());
        u.setNombre(dto.getNombre().trim());
        u.setApellido(dto.getApellido().trim());
        u.setCorreo(dto.getCorreo().trim().toLowerCase());
        u.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        u.setFechaNacimiento(dto.getFechaNacimiento());
        u.setRol(rol);

        return usuarioRepository.save(u);
    }

    @Transactional
    public void cambiarRol(Integer id, Integer nuevoRolId) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Rol rol = rolRepository.findById(nuevoRolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        u.setRol(rol);
        usuarioRepository.save(u);
    }

    @Transactional
    public void toggleActivo(Integer id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setActivo(!u.getActivo());
        usuarioRepository.save(u);
    }

    @Transactional
    public void solicitarResetPassword(Integer id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        tokenRepository.deleteByUsuarioId(id);

        String token = generarToken(u, TokenPasswordReset.TipoToken.RESET, 24);
        emailService.enviarResetPassword(u.getCorreo(), u.getNombre() + " " + u.getApellido(), token);
    }

    public Optional<TokenPasswordReset> validarToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(TokenPasswordReset::estaVigente);
    }

    @Transactional
    public void establecerPassword(TokenPasswordDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }
        if (dto.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }

        TokenPasswordReset tokenObj = tokenRepository.findByToken(dto.getToken())
                .filter(TokenPasswordReset::estaVigente)
                .orElseThrow(() -> new RuntimeException("El enlace no es válido o ya expiró"));

        Usuario u = tokenObj.getUsuario();
        u.setContrasena(passwordEncoder.encode(dto.getPassword()));
        u.setActivo(true); // activar la cuenta al establecer contraseña
        usuarioRepository.save(u);

        tokenObj.setUsado(true);
        tokenRepository.save(tokenObj);
    }

    private String generarToken(Usuario u, TokenPasswordReset.TipoToken tipo, int horasExpiracion) {
        String tokenStr = UUID.randomUUID().toString();
        TokenPasswordReset t = new TokenPasswordReset();
        t.setUsuario(u);
        t.setToken(tokenStr);
        t.setTipo(tipo);
        t.setUsado(false);
        t.setFechaExpiracion(LocalDateTime.now().plusHours(horasExpiracion));
        tokenRepository.save(t);
        return tokenStr;
    }

    private void validarDniUnico(String dni, Integer excludeId) {
        usuarioRepository.findByDni(dni.trim()).ifPresent(u -> {
            if (excludeId == null || !u.getId().equals(excludeId)) {
                throw new RuntimeException("Ya existe un usuario con el DNI: " + dni);
            }
        });
    }

    private void validarCorreoUnico(String correo, Integer excludeId) {
        usuarioRepository.findByCorreo(correo.trim().toLowerCase()).ifPresent(u -> {
            if (excludeId == null || !u.getId().equals(excludeId)) {
                throw new RuntimeException("Ya existe un usuario con el correo: " + correo);
            }
        });
    }
}