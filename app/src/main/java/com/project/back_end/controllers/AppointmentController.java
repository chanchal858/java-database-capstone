package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.model.Appointment;
import com.project.back_end.service.AppointmentService;
import com.project.back_end.service.Service;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(
            AppointmentService appointmentService,
            Service service) {

        this.appointmentService = appointmentService;
        this.service = service;
    }

    // ---------------------------------------------------------
    // Get Appointments
    // ---------------------------------------------------------

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "doctor");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        try {

            LocalDate appointmentDate =
                    LocalDate.parse(date);

            Map<String, Object> appointments =
                    appointmentService.getAppointment(
                            patientName,
                            appointmentDate,
                            token);

            return ResponseEntity.ok(appointments);

        } catch (Exception e) {

            response.put(
                    "error",
                    "Invalid date format");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Book Appointment
    // ---------------------------------------------------------

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        try {

            int validation =
                    service.validateAppointment(appointment);

            if (validation == -1) {

                response.put(
                        "error",
                        "Doctor not found");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            if (validation == 0) {

                response.put(
                        "error",
                        "Appointment time is not available");

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(response);
            }

            int result =
                    appointmentService.bookAppointment(
                            appointment);

            if (result == 1) {

                response.put(
                        "message",
                        "Appointment booked successfully");

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response);
            }

            response.put(
                    "error",
                    "Failed to book appointment");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Failed to book appointment");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Update Appointment
    // ---------------------------------------------------------

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {

            Map<String, String> response = new HashMap<>();

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        return appointmentService.updateAppointment(
                appointment);
    }

    // ---------------------------------------------------------
    // Cancel Appointment
    // ---------------------------------------------------------

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "patient");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {

            Map<String, String> response = new HashMap<>();

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        return appointmentService.cancelAppointment(
                id,
                token);
    }
}
