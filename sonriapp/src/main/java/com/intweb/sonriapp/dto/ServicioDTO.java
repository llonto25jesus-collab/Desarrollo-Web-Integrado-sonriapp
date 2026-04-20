package com.intweb.sonriapp.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDTO {

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    @Max(value = 480, message = "La duración no puede superar las 8 horas (480 minutos)")
    private Integer duracion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a S/ 0.00")
    @DecimalMax(value = "99999.99", message = "El precio ingresado es demasiado alto")
    private BigDecimal precio;

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String descripcion;
}