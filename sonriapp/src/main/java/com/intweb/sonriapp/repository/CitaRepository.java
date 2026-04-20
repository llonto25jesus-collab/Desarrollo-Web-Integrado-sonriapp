package com.intweb.sonriapp.repository;

import com.intweb.sonriapp.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

    // Citas entre dos fechas (para contar el mes)
    long countByFechaBetween(LocalDate inicio, LocalDate fin);

    // Citas del día de hoy para el dashboard
    List<Cita> findByFechaOrderByHoraAsc(LocalDate fecha);

    // Suma de ingresos del mes (precio del servicio x citas completadas)
    @Query("""
        SELECT COALESCE(SUM(c.servicio.precio), 0)
        FROM Cita c
        WHERE c.fecha BETWEEN :inicio AND :fin
        AND c.estado = 'Completada'
    """)
    BigDecimal sumIngresosByFechaBetween(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );
}