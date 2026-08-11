package com.project.back_end.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;

    public PrescriptionController(
            PrescriptionService prescriptionService,
            Service service) {

        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    // ---------------------------------------------------------
    // Save Prescription
    // ---------------------------------------------------------

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @RequestBody Prescription prescription) {

        // Validate doctor token
        ResponseEntity<Map<String, String>> tokenResponse =
                service.validateToken(token, "doctor");

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {

            Map<String, String> response = new HashMap<>();

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        // Save prescription
        return prescriptionService.savePrescription(prescription);
    }

    // ---------------------------------------------------------
    // Get Prescription By Appointment ID
    // ---------------------------------------------------------

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        // Validate doctor token
        ResponseEntity<Map<String, String>> tokenResponse =
                service.validateToken(token, "doctor");

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {

            Map<String, Object> response = new HashMap<>();

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        // Get prescription
        return prescriptionService.getPrescription(appointmentId);
    }
}
