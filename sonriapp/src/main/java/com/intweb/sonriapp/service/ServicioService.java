package com.intweb.sonriapp.service;

import com.intweb.sonriapp.dto.ServicioDTO;
import com.intweb.sonriapp.model.Servicio;
import com.intweb.sonriapp.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public List<Servicio> listarTodos() {
        return servicioRepository.findAllByOrderByNombreAsc();
    }

    public Optional<Servicio> obtenerPorId(Integer id) {
        return servicioRepository.findById(id);
    }

    @Transactional
    public Servicio guardar(ServicioDTO dto) {
        validarDTO(dto);
        if (servicioRepository.existsByNombreIgnoreCase(dto.getNombre().trim())) {
            throw new RuntimeException("Ya existe un servicio con el nombre: " + dto.getNombre());
        }
        return servicioRepository.save(mapToEntity(new Servicio(), dto));
    }

    @Transactional
    public Servicio actualizar(Integer id, ServicioDTO dto) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
        validarDTO(dto);
        if (servicioRepository.existsByNombreIgnoreCaseAndIdNot(dto.getNombre().trim(), id)) {
            throw new RuntimeException("Ya existe un servicio con el nombre: " + dto.getNombre());
        }
        return servicioRepository.save(mapToEntity(servicio, dto));
    }

    @Transactional
    public void eliminar(Integer id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
        try {
            servicioRepository.delete(servicio);
        } catch (Exception e) {
            // La FK en citas tiene ON DELETE RESTRICT, así que si hay citas asociadas fallará
            throw new RuntimeException("No se puede eliminar el servicio porque tiene citas asociadas");
        }
    }

    // ─── Validaciones de negocio ──────────────────────────────────────────────
    private void validarDTO(ServicioDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del servicio es obligatorio");
        }
        if (dto.getNombre().trim().length() > 150) {
            throw new RuntimeException("El nombre no puede superar los 150 caracteres");
        }
        if (dto.getDuracion() == null || dto.getDuracion() <= 0) {
            throw new RuntimeException("La duración debe ser mayor a 0 minutos");
        }
        if (dto.getDuracion() > 480) {
            throw new RuntimeException("La duración no puede superar las 8 horas (480 minutos)");
        }
        if (dto.getPrecio() == null || dto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio debe ser mayor a S/ 0.00");
        }
        if (dto.getPrecio().compareTo(new BigDecimal("99999.99")) > 0) {
            throw new RuntimeException("El precio ingresado es demasiado alto");
        }
    }

    private Servicio mapToEntity(Servicio servicio, ServicioDTO dto) {
        servicio.setNombre(dto.getNombre().trim());
        servicio.setDuracion(dto.getDuracion());
        servicio.setPrecio(dto.getPrecio());
        servicio.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null);
        return servicio;
    }
}