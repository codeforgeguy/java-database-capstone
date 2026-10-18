package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service;

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Unauthorized or invalid token");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = appointmentService.getAppointment(patientName, date, token);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            response.put("message", "Unauthorized or invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int appointmentValidation = service.validateAppointment(appointment);
        if (appointmentValidation == -1) {
            response.put("message", "Doctor does not exist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (appointmentValidation == 0) {
            response.put("message", "Appointment slot is unavailable");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        int bookResult = appointmentService.bookAppointment(appointment);
        if (bookResult == 1) {
            response.put("message", "Appointment booked successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.put("message", "Failed to book appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Unauthorized or invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Unauthorized or invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        return appointmentService.cancelAppointment(id, token);
    }
}