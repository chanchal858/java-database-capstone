package com.project.back_end.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(
            PatientService patientService,
            Service service) {

        this.patientService = patientService;
        this.service = service;
    }

    // ---------------------------------------------------------
    // Get Patient Details
    // ---------------------------------------------------------

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatientDetails(
            @PathVariable String token) {

        if (!service.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {

            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        return patientService.getPatientDetails(token);
    }

    // ---------------------------------------------------------
    // Create Patient
    // ---------------------------------------------------------

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(
            @RequestBody Patient patient) {

        Map<String, String> response = new HashMap<>();

        try {

            if (patient == null) {
                response.put("error", "Invalid patient data");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            // true means patient does not already exist
            boolean isValid = service.validatePatient(patient);

            if (!isValid) {

                response.put(
                        "error",
                        "Patient with email id or phone no already exist");

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(response);
            }

            int result = patientService.createPatient(patient);

            if (result == 1) {

                response.put(
                        "message",
                        "Signup successful");

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response);
            }

            response.put(
                    "error",
                    "Internal server error");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Internal server error");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Patient Login
    // ---------------------------------------------------------

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(
            @RequestBody Login login) {

        return service.validatePatientLogin(login);
    }

    // ---------------------------------------------------------
    // Get Patient Appointments
    // ---------------------------------------------------------

    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token) {

        if (!service.validateToken(token, "patient")
                .getStatusCode().is2xxSuccessful()) {

            Map<String, Object> response = new HashMap<>();

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        return patientService.getPatientAppointment(id, token);
    }

    // ---------------------------------------------------------
    // Filter Patient Appointments
    // ---------------------------------------------------------

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        if (!service.validateToken(token, "patient")
                .getStatusCode().is2xxSuccessful()) {

            Map<String, Object> response = new HashMap<>();

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        return service.filterPatient(
                condition,
                name,
                token);
    }
}
