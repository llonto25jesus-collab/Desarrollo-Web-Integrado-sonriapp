package com.intweb.sonriapp.service;

import com.intweb.sonriapp.dto.PacienteDTO;
import com.intweb.sonriapp.model.Paciente;
import com.intweb.sonriapp.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> obtenerPorId(Integer id) {
        return pacienteRepository.findById(id);
    }

    @Transactional
    public Paciente guardar(PacienteDTO dto) {
        if (pacienteRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("Ya existe un paciente con el DNI: " + dto.getDni());
        }
        return pacienteRepository.save(mapToEntity(new Paciente(), dto));
    }

    @Transactional
    public Paciente actualizar(Integer id, PacienteDTO dto) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));

        if (!paciente.getDni().equals(dto.getDni()) && pacienteRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("Ya existe un paciente con el DNI: " + dto.getDni());
        }

        return pacienteRepository.save(mapToEntity(paciente, dto));
    }

    public boolean existeDni(String dni) {
        return pacienteRepository.existsByDni(dni);
    }

    private Paciente mapToEntity(Paciente paciente, PacienteDTO dto) {
        paciente.setDni(dto.getDni());
        paciente.setNombre(dto.getNombre());
        paciente.setApellido(dto.getApellido());
        paciente.setTelefono(dto.getTelefono());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setGenero(Paciente.Genero.valueOf(dto.getGenero()));
        paciente.setDireccion(dto.getDireccion());
        paciente.setContactoEmergencia(dto.getContactoEmergencia());
        paciente.setAntecedentes(dto.getAntecedentes());
        return paciente;
    }
}