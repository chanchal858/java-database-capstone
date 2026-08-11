package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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

import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(
            DoctorService doctorService,
            Service service) {

        this.doctorService = doctorService;
        this.service = service;
    }

    // ---------------------------------------------------------
    // Get Doctor Availability
    // ---------------------------------------------------------

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable LocalDate date,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, user);

        if (!validation.getStatusCode().is2xxSuccessful()) {

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        try {

            List<String> availability =
                    doctorService.getDoctorAvailability(
                            doctorId,
                            date);

            response.put("availability", availability);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Failed to retrieve doctor availability");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Get All Doctors
    // ---------------------------------------------------------

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Doctor> doctors =
                    doctorService.getDoctors();

            response.put("doctors", doctors);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Failed to retrieve doctors");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Add Doctor
    // ---------------------------------------------------------

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        int result =
                doctorService.saveDoctor(doctor);

        if (result == 1) {

            response.put(
                    "message",
                    "Doctor added to db");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }

        if (result == -1) {

            response.put(
                    "error",
                    "Doctor already exists");

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        response.put(
                "error",
                "Some internal error occurred");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    // ---------------------------------------------------------
    // Doctor Login
    // ---------------------------------------------------------

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(
            @RequestBody Login login) {

        return doctorService.validateDoctor(login);
    }

    // ---------------------------------------------------------
    // Update Doctor
    // ---------------------------------------------------------

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        int result =
                doctorService.updateDoctor(doctor);

        if (result == 1) {

            response.put(
                    "message",
                    "Doctor updated");

            return ResponseEntity.ok(response);
        }

        if (result == -1) {

            response.put(
                    "error",
                    "Doctor not found");

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        response.put(
                "error",
                "Some internal error occurred");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    // ---------------------------------------------------------
    // Delete Doctor
    // ---------------------------------------------------------

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable long id,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {

            response.put(
                    "error",
                    "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        int result =
                doctorService.deleteDoctor(id);

        if (result == 1) {

            response.put(
                    "message",
                    "Doctor deleted successfully");

            return ResponseEntity.ok(response);
        }

        if (result == -1) {

            response.put(
                    "error",
                    "Doctor not found with id");

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        response.put(
                "error",
                "Some internal error occurred");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    // ---------------------------------------------------------
    // Filter Doctors
    // ---------------------------------------------------------

    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        try {

            Map<String, Object> response =
                    service.filterDoctor(
                            name,
                            speciality,
                            time);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "error",
                    "Failed to filter doctors");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
