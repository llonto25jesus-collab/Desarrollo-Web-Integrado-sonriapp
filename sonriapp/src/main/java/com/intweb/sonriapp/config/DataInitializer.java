package com.intweb.sonriapp.config;

import com.intweb.sonriapp.model.Rol;
import com.intweb.sonriapp.model.Usuario;
import com.intweb.sonriapp.repository.RolRepository;
import com.intweb.sonriapp.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        // ===== ROLES =====
        Rol adminRol = crearRolSiNoExiste("ROLE_ADMIN");
        Rol doctorRol = crearRolSiNoExiste("ROLE_DOCTOR");
        Rol pacienteRol = crearRolSiNoExiste("ROLE_PACIENTE");

        // ===== USUARIOS =====
        crearUsuario("admin@gmail.com", "123456", adminRol);
        crearUsuario("doctor@gmail.com", "123456", doctorRol);
        crearUsuario("paciente@gmail.com", "123456", pacienteRol);

        System.out.println("✅ Roles y usuarios creados");
    }

    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol rol = new Rol();
                    rol.setNombre(nombre);
                    return rolRepository.save(rol);
                });
    }

    private void crearUsuario(String correo, String password, Rol rol) {
        if (usuarioRepository.findByCorreo(correo).isEmpty()) {
            Usuario u = new Usuario();
            u.setCorreo(correo);
            u.setPassword(passwordEncoder.encode(password));
            u.setRol(rol);
            usuarioRepository.save(u);
        }
    }
}