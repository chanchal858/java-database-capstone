package com.project.back_end.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.model.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    // ---------------------------------------------------------
    // Save Prescription
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, String>> savePrescription(
            Prescription prescription) {

        Map<String, String> response = new HashMap<>();

        try {
            prescriptionRepository.save(prescription);

            response.put("message", "Prescription saved");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {

            response.put("message", "Failed to save prescription");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Get Prescription by Appointment ID
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, Object>> getPrescription(
            Long appointmentId) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Prescription> prescriptions =
                    prescriptionRepository.findByAppointmentId(appointmentId);

            response.put("prescriptions", prescriptions);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (Exception e) {

            response.put("message", "Failed to retrieve prescription");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
