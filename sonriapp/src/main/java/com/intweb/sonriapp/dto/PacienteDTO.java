package com.intweb.sonriapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {
    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String genero;
    private String direccion;
    private String contactoEmergencia;
    private String antecedentes;
}