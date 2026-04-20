package com.intweb.sonriapp.service;

import com.intweb.sonriapp.model.Cita;
import com.intweb.sonriapp.model.DashboardStats;
import com.intweb.sonriapp.repository.CitaRepository;
import com.intweb.sonriapp.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;

    public DashboardStats obtenerEstadisticas() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        long totalPacientes = pacienteRepository.count();
        long citasDelMes = citaRepository.countByFechaBetween(inicioMes, finMes);
        BigDecimal ingresos = citaRepository.sumIngresosByFechaBetween(inicioMes, finMes);

        return new DashboardStats(totalPacientes, citasDelMes, ingresos);
    }

    public List<Cita> obtenerCitasDeHoy() {
        return citaRepository.findByFechaOrderByHoraAsc(LocalDate.now());
    }
}