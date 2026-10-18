package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private com.project.back_end.services.Service service;

    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
            if (existingOpt.isEmpty()) {
                response.put("message", "Appointment not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            int validationCode = service.validateAppointment(appointment);
            if (validationCode == -1) {
                response.put("message", "Doctor does not exist");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (validationCode == 0) {
                response.put("message", "Appointment slot unavailable");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
            if (appointmentOpt.isEmpty()) {
                response.put("message", "Appointment not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Appointment appointment = appointmentOpt.get();
            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null || !appointment.getPatient().getId().equals(patient.getId())) {
                response.put("message", "Unauthorized to cancel this appointment");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            Doctor doctor = doctorRepository.findByEmail(email);
            if (doctor == null) {
                response.put("appointments", Collections.emptyList());
                return response;
            }

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            List<Appointment> appointments;
            if (pname != null && !pname.trim().isEmpty() && !"null".equalsIgnoreCase(pname)) {
                appointments = appointmentRepository
                        .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                doctor.getId(), pname, startOfDay, endOfDay);
            } else {
                appointments = appointmentRepository
                        .findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), startOfDay, endOfDay);
            }

            List<AppointmentDTO> dtos = appointments.stream().map(a -> new AppointmentDTO(
                    a.getId(),
                    a.getDoctor() != null ? a.getDoctor().getId() : null,
                    a.getDoctor() != null ? a.getDoctor().getName() : null,
                    a.getPatient() != null ? a.getPatient().getId() : null,
                    a.getPatient() != null ? a.getPatient().getName() : null,
                    a.getPatient() != null ? a.getPatient().getEmail() : null,
                    a.getPatient() != null ? a.getPatient().getPhone() : null,
                    a.getPatient() != null ? a.getPatient().getAddress() : null,
                    a.getAppointmentTime(),
                    a.getStatus()
            )).collect(Collectors.toList());

            response.put("appointments", dtos);
            return response;
        } catch (Exception e) {
            response.put("appointments", Collections.emptyList());
            return response;
        }
    }
}