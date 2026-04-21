package com.intweb.sonriapp.service;

import com.intweb.sonriapp.dto.CitaDTO;
import com.intweb.sonriapp.model.*;
import com.intweb.sonriapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository             citaRepository;
    private final PacienteRepository         pacienteRepository;
    private final UsuarioRepository          usuarioRepository;
    private final ServicioRepository         servicioRepository;
    private final HorarioConsultorioRepository horarioRepository;

    public List<Cita> listarTodas() {
        return citaRepository.findAllByOrderByFechaDescHoraDesc();
    }

    public List<Cita> listarPorFecha(LocalDate fecha) {
        return citaRepository.findByFechaOrderByHoraAsc(fecha);
    }

    public Optional<Cita> obtenerPorId(Integer id) {
        return citaRepository.findById(id);
    }

    public List<LocalTime> horasOcupadasPorDoctorYFecha(Integer doctorId, LocalDate fecha) {
        return citaRepository.findHorasOcupadasByDoctorAndFecha(doctorId, fecha);
    }


    @Transactional
    public Cita guardar(CitaDTO dto) {
        Servicio servicio = obtenerServicio(dto.getServicioId());
        validarFechaHora(dto.getFecha(), dto.getHora());
        validarSolapamientoDoctor(dto.getDoctorId(), dto.getFecha(), dto.getHora(),
                servicio.getDuracion(), null);
        validarSolapamientoPaciente(dto.getPacienteId(), dto.getFecha(), dto.getHora(),
                servicio.getDuracion(), null);

        Cita cita = new Cita();
        return citaRepository.save(mapToEntity(cita, dto, servicio));
    }


    @Transactional
    public Cita actualizar(Integer id, CitaDTO dto) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));

        Servicio servicio = obtenerServicio(dto.getServicioId());
        validarFechaHora(dto.getFecha(), dto.getHora());
        validarSolapamientoDoctor(dto.getDoctorId(), dto.getFecha(), dto.getHora(),
                servicio.getDuracion(), id);
        validarSolapamientoPaciente(dto.getPacienteId(), dto.getFecha(), dto.getHora(),
                servicio.getDuracion(), id);

        return citaRepository.save(mapToEntity(cita, dto, servicio));
    }


    @Transactional
    public Cita cambiarEstado(Integer id, Cita.Estado nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }


    @Transactional
    public void eliminar(Integer id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
        citaRepository.delete(cita);
    }


    public List<HorarioConsultorio> listarHorarios() {
        return horarioRepository.findAllByOrderByDiaSemanaAsc();
    }

    @Transactional
    public HorarioConsultorio guardarHorario(Integer diaSemana, Boolean activo,
                                             LocalTime horaInicio, LocalTime horaFin) {
        HorarioConsultorio horario = horarioRepository.findByDiaSemana(diaSemana)
                .orElseGet(() -> {
                    HorarioConsultorio h = new HorarioConsultorio();
                    h.setDiaSemana(diaSemana);
                    return h;
                });
        if (activo && horaFin.isBefore(horaInicio)) {
            throw new RuntimeException("La hora de fin no puede ser anterior a la hora de inicio");
        }
        horario.setActivo(activo);
        horario.setHoraInicio(horaInicio);
        horario.setHoraFin(horaFin);
        return horarioRepository.save(horario);
    }

    private void validarFechaHora(LocalDate fecha, LocalTime hora) {
        LocalDateTime ahora = LocalDateTime.now();

        if (fecha.isBefore(ahora.toLocalDate())) {
            throw new RuntimeException("No se pueden crear citas en fechas pasadas");
        }

        // Si es hoy, la hora no puede ser menor o igual a la hora actual
        if (fecha.isEqual(ahora.toLocalDate()) && !hora.isAfter(ahora.toLocalTime())) {
            throw new RuntimeException(
                    "No se pueden agendar citas en horarios ya pasados del día de hoy");
        }

        int diaSemana = fecha.getDayOfWeek().getValue();
        HorarioConsultorio horario = horarioRepository.findByDiaSemana(diaSemana)
                .orElseThrow(() -> new RuntimeException(
                        "No hay configuración de horario para ese día"));

        if (!horario.getActivo()) {
            throw new RuntimeException(
                    "El consultorio no atiende los " + horario.getNombreDia());
        }

        if (hora.isBefore(horario.getHoraInicio())
                || !hora.isBefore(horario.getHoraFin())) {
            throw new RuntimeException(
                    "La hora debe estar entre " + horario.getHoraInicio()
                            + " y " + horario.getHoraFin()
                            + " (" + horario.getNombreDia() + ")");
        }
    }

    private void validarSolapamientoDoctor(Integer doctorId, LocalDate fecha,
                                           LocalTime hora, int duracionNueva,
                                           Integer excludeId) {
        LocalTime horaFinNueva = hora.plusMinutes(duracionNueva + 15);

        List<Cita> solapadas = citaRepository.findCitasSolapadasDoctor(
                doctorId, fecha, hora, horaFinNueva, excludeId);

        if (!solapadas.isEmpty()) {
            Cita conflicto = solapadas.get(0);
            LocalTime bloqueadoHasta = conflicto.getHora()
                    .plusMinutes(conflicto.getServicio().getDuracion() + 15);
            throw new RuntimeException(
                    "El doctor tiene una cita a las " + conflicto.getHora()
                            + " (servicio: " + conflicto.getServicio().getNombre()
                            + ") que ocupa hasta las " + bloqueadoHasta
                            + ". Elija un horario posterior.");
        }
    }

    private void validarSolapamientoPaciente(Integer pacienteId, LocalDate fecha,
                                             LocalTime hora, int duracionNueva,
                                             Integer excludeId) {
        LocalTime horaFinNueva = hora.plusMinutes(duracionNueva + 15);

        List<Cita> solapadas = citaRepository.findCitasSolapadasPaciente(
                pacienteId, fecha, hora, horaFinNueva, excludeId);

        if (!solapadas.isEmpty()) {
            Cita conflicto = solapadas.get(0);
            LocalTime bloqueadoHasta = conflicto.getHora()
                    .plusMinutes(conflicto.getServicio().getDuracion() + 15);
            throw new RuntimeException(
                    "El paciente ya tiene una cita a las " + conflicto.getHora()
                            + " (servicio: " + conflicto.getServicio().getNombre()
                            + ") que ocupa hasta las " + bloqueadoHasta
                            + ". Elija un horario posterior.");
        }
    }


    private Servicio obtenerServicio(Integer servicioId) {
        return servicioRepository.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }

    private Cita mapToEntity(Cita cita, CitaDTO dto, Servicio servicio) {
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario doctor = usuarioRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        if (doctor.getRol() == null || doctor.getRol().getId() != 2) {
            throw new RuntimeException("El usuario seleccionado no es un Odontólogo");
        }

        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setServicio(servicio);
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setEstado(dto.getEstado() != null ? dto.getEstado() : Cita.Estado.Pendiente);
        cita.setNotas(dto.getNotas() != null ? dto.getNotas().trim() : null);
        return cita;
    }
}