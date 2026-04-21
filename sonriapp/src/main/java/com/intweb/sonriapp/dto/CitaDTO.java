package com.intweb.sonriapp.dto;

import com.intweb.sonriapp.model.Cita;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTO {

    @NotNull(message = "El paciente es obligatorio")
    private Integer pacienteId;

    @NotNull(message = "El doctor es obligatorio")
    private Integer doctorId;

    @NotNull(message = "El servicio es obligatorio")
    private Integer servicioId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    private Cita.Estado estado;

    private String notas;
}