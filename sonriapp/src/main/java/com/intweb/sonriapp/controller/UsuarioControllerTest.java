package com.intweb.sonriapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private UsuarioController usuarioController;

    @Test
    void contextLoads() {
        assertNotNull(usuarioController);
    }

    @Test
    void testControllerInstancia() {
        assertTrue(usuarioController != null);
    }

}