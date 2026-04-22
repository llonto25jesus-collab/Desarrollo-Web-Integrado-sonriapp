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
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Test
    void contextLoads() {
        assertNotNull(authController);
    }

    @Test
    void testLoginMetodo() {

        String view = authController.login();

        assertNotNull(view);
        assertEquals("login", view);

    }

}