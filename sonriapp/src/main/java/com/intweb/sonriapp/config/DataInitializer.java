package com.intweb.sonriapp.config;

import com.intweb.sonriapp.model.Rol;
import com.intweb.sonriapp.model.Usuario;
import com.intweb.sonriapp.repository.RolRepository;
import com.intweb.sonriapp.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        Rol admin  = rolRepository.findByNombre("Administrador").orElseThrow();
        Rol doctor = rolRepository.findByNombre("Odontologo").orElseThrow();
        Rol recep  = rolRepository.findByNombre("Recepcionista").orElseThrow();

        crearUsuario("12345678", "Admin",  "Sistema", "admin@gmail.com",  admin);
        crearUsuario("87654321", "Doctor", "Ejemplo", "doctor@gmail.com", doctor);
        crearUsuario("11223344", "Recep",  "Ejemplo", "recep@gmail.com",  recep);

        System.out.println("✅ Usuarios de prueba cargados (perfil dev)");
    }

    private void crearUsuario(String dni, String nombre,
                              String apellido, String correo, Rol rol) {
        if (usuarioRepository.findByCorreo(correo).isEmpty()) {
            Usuario u = new Usuario();
            u.setDni(dni);
            u.setNombre(nombre);
            u.setApellido(apellido);
            u.setCorreo(correo);
            u.setContrasena(passwordEncoder.encode("123456"));
            u.setFechaNacimiento(LocalDate.of(1990, 1, 1));
            u.setRol(rol);
            u.setActivo(true);
            usuarioRepository.save(u);
        }
    }
}