package com.intweb.sonriapp.repository;

import com.intweb.sonriapp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByDni(String dni);
    boolean existsByCorreo(String correo);
    List<Usuario> findAllByOrderByNombreAsc();

    List<Usuario> findByRolIdAndActivoTrue(Integer rolId);
}