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

        // Crear rol si no existe
        Rol rol = rolRepository.findByNombre("ROLE_ADMIN")
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setNombre("ROLE_ADMIN");
                    return rolRepository.save(r);
                });

        // Crear usuario si no existe
        if (usuarioRepository.findByCorreo("admin@gmail.com").isEmpty()) {

            Usuario usuario = new Usuario();
            usuario.setCorreo("admin@gmail.com");
            usuario.setPassword(passwordEncoder.encode("123456"));
            usuario.setRol(rol);

            usuarioRepository.save(usuario);

            System.out.println("✅ Usuario admin creado automáticamente");
        }
    }
}