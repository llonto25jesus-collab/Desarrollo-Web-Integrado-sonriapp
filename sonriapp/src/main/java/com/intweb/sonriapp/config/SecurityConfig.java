package com.intweb.sonriapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()

                        // ADMIN (solo admin)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // DOCTOR puede ser DOCTOR o ADMIN
                        .requestMatchers("/doctor/**").hasAnyRole("DOCTOR", "ADMIN")

                        // PACIENTE (solo paciente)
                        .requestMatchers("/paciente/**").hasRole("PACIENTE")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/redirect", true) // 👈 importante
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout"));

        return http.build();
    }
}