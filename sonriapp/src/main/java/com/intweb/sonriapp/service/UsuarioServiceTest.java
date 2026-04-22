package com.intweb.sonriapp.service;

import com.intweb.sonriapp.model.Usuario;
import com.intweb.sonriapp.repository.RolRepository;
import com.intweb.sonriapp.repository.TokenPasswordResetRepository;
import com.intweb.sonriapp.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private TokenPasswordResetRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarTodos() {

        Usuario u = new Usuario();
        u.setNombre("Juan");

        when(usuarioRepository.findAllByOrderByNombreAsc())
                .thenReturn(List.of(u));

        List<Usuario> usuarios = usuarioService.listarTodos();

        assertEquals(1, usuarios.size());
        assertEquals("Juan", usuarios.get(0).getNombre());
    }

}