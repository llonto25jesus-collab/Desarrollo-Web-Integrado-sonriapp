package com.intweb.sonriapp.repository;

import com.intweb.sonriapp.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
    Optional<Paciente> findByDni(String dni);
    boolean existsByDni(String dni);
}