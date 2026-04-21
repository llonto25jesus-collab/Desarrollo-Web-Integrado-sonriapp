package com.intweb.sonriapp.repository;

import com.intweb.sonriapp.model.HorarioConsultorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HorarioConsultorioRepository extends JpaRepository<HorarioConsultorio, Integer> {

    List<HorarioConsultorio> findAllByOrderByDiaSemanaAsc();

    Optional<HorarioConsultorio> findByDiaSemana(Integer diaSemana);
}