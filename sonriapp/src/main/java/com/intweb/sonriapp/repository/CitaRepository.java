package com.intweb.sonriapp.repository;

import com.intweb.sonriapp.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

    long countByFechaBetween(LocalDate inicio, LocalDate fin);
    List<Cita> findByFechaOrderByHoraAsc(LocalDate fecha);

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

    List<Cita> findAllByOrderByFechaDescHoraDesc();

    boolean existsByDoctorIdAndFechaAndHora(Integer doctorId, LocalDate fecha, LocalTime hora);
    boolean existsByDoctorIdAndFechaAndHoraAndIdNot(Integer doctorId, LocalDate fecha, LocalTime hora, Integer id);

    @Query("SELECT c.hora FROM Cita c WHERE c.doctor.id = :doctorId AND c.fecha = :fecha")
    List<LocalTime> findHorasOcupadasByDoctorAndFecha(
            @Param("doctorId") Integer doctorId,
            @Param("fecha") LocalDate fecha
    );

    List<Cita> findByFechaBetweenOrderByFechaAscHoraAsc(LocalDate inicio, LocalDate fin);

    @Query("""
        SELECT c FROM Cita c
        WHERE c.doctor.id = :doctorId
          AND c.fecha = :fecha
          AND (:excludeId IS NULL OR c.id <> :excludeId)
          AND c.hora < :nuevaHoraFin
          AND FUNCTION('ADDTIME', c.hora,
                FUNCTION('SEC_TO_TIME', (c.servicio.duracion + 15) * 60)
              ) > :nuevaHora
    """)
    List<Cita> findCitasSolapadasDoctor(
            @Param("doctorId") Integer doctorId,
            @Param("fecha") LocalDate fecha,
            @Param("nuevaHora") LocalTime nuevaHora,
            @Param("nuevaHoraFin") LocalTime nuevaHoraFin,
            @Param("excludeId") Integer excludeId
    );


    @Query("""
        SELECT c FROM Cita c
        WHERE c.paciente.id = :pacienteId
          AND c.fecha = :fecha
          AND (:excludeId IS NULL OR c.id <> :excludeId)
          AND c.hora < :nuevaHoraFin
          AND FUNCTION('ADDTIME', c.hora,
                FUNCTION('SEC_TO_TIME', (c.servicio.duracion + 15) * 60)
              ) > :nuevaHora
    """)
    List<Cita> findCitasSolapadasPaciente(
            @Param("pacienteId") Integer pacienteId,
            @Param("fecha") LocalDate fecha,
            @Param("nuevaHora") LocalTime nuevaHora,
            @Param("nuevaHoraFin") LocalTime nuevaHoraFin,
            @Param("excludeId") Integer excludeId
    );
}