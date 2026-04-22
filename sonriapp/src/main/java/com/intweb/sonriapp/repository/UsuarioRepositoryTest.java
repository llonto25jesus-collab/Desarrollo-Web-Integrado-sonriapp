package com.intweb.sonriapp.repository;

import com.intweb.sonriapp.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testGuardarUsuario() {

        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");

        usuarioRepository.save(usuario);

        assertNotNull(usuario.getId());

    }

    @Test
    void testListarUsuarios() {

        Usuario usuario = new Usuario();
        usuario.setNombre("Pedro");

        usuarioRepository.save(usuario);

        List<Usuario> lista = usuarioRepository.findAll();

        assertFalse(lista.isEmpty());
    }

}