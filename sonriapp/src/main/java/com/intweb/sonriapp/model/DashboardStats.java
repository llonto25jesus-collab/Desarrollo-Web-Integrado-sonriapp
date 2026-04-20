package com.intweb.sonriapp.model;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalPacientes;
    private long citasDelMes;
    private BigDecimal ingresosDelMes; // solo para Administrador
}