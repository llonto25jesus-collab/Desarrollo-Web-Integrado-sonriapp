package com.intweb.sonriapp.repository;

import com.intweb.sonriapp.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
}