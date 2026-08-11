package com.project.back_end.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService) {

        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // ---------------------------------------------------------
    // Validate Token
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, String>> validateToken(
            String token,
            String user) {

        Map<String, String> response = new HashMap<>();

        if (token == null || token.isBlank()) {
            response.put("error", "Token is missing");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        boolean valid = tokenService.validateToken(token, user);

        if (!valid) {
            response.put("error", "Invalid or expired token");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------
    // Validate Admin Login
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, String>> validateAdmin(
            Admin receivedAdmin) {

        Map<String, String> response = new HashMap<>();

        try {

            Admin admin =
                    adminRepository.findByUsername(
                            receivedAdmin.getUsername());

            if (admin == null) {
                response.put("error", "Invalid username or password");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            if (!admin.getPassword()
                    .equals(receivedAdmin.getPassword())) {

                response.put("error", "Invalid username or password");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            String token =
                    tokenService.generateToken(admin.getUsername());

            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("error", "Internal server error");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Filter Doctors
    // ---------------------------------------------------------
    public Map<String, Object> filterDoctor(
            String name,
            String specialty,
            String time) {

        if (name == null) {
            name = "";
        }

        if (specialty == null) {
            specialty = "";
        }

        if (time == null) {
            time = "";
        }

        name = name.trim();
        specialty = specialty.trim();
        time = time.trim();

        // No filters
        if (name.isEmpty()
                && specialty.isEmpty()
                && time.isEmpty()) {

            Map<String, Object> response = new HashMap<>();

            response.put("doctors", doctorService.getDoctors());

            return response;
        }

        // Name + Specialty + Time
        if (!name.isEmpty()
                && !specialty.isEmpty()
                && !time.isEmpty()) {

            return doctorService
                    .filterDoctorsByNameSpecilityandTime(
                            name,
                            specialty,
                            time);
        }

        // Name + Time
        if (!name.isEmpty() && !time.isEmpty()) {

            return doctorService
                    .filterDoctorByNameAndTime(
                            name,
                            time);
        }

        // Name + Specialty
        if (!name.isEmpty() && !specialty.isEmpty()) {

            return doctorService
                    .filterDoctorByNameSpecility(
                            name,
                            specialty);
        }

        // Specialty + Time
        if (!specialty.isEmpty() && !time.isEmpty()) {

            return doctorService
                    .filterDoctorByTimeAndSpecility(
                            specialty,
                            time);
        }

        // Name only
        if (!name.isEmpty()) {

            return doctorService.findDoctorByName(name);
        }

        // Specialty only
        if (!specialty.isEmpty()) {

            return doctorService
                    .filterDoctorBySpecility(specialty);
        }

        // Time only
        return doctorService.filterDoctorsByTime(time);
    }

    // ---------------------------------------------------------
    // Validate Appointment
    // ---------------------------------------------------------
    public int validateAppointment(
            Appointment appointment) {

        if (appointment == null
                || appointment.getDoctor() == null
                || appointment.getAppointmentTime() == null) {

            return 0;
        }

        Long doctorId =
                appointment.getDoctor().getId();

        if (doctorId == null) {
            return -1;
        }

        Optional<Doctor> doctor =
                doctorRepository.findById(doctorId);

        if (doctor.isEmpty()) {
            return -1;
        }

        List<String> availableTimes =
                doctorService.getDoctorAvailability(
                        doctorId,
                        appointment.getAppointmentTime()
                                .toLocalDate());

        String appointmentTime =
                appointment.getAppointmentTime()
                        .toLocalTime()
                        .toString();

        // Handle HH:mm:ss vs HH:mm
        boolean available = availableTimes.stream()
                .anyMatch(time -> time.equals(appointmentTime)
                        || time.startsWith(
                                appointmentTime.substring(0, 5)));

        return available ? 1 : 0;
    }

    // ---------------------------------------------------------
    // Validate Patient Registration
    // ---------------------------------------------------------
    public boolean validatePatient(Patient patient) {

        Patient existingPatient =
                patientRepository.findByEmailOrPhone(
                        patient.getEmail(),
                        patient.getPhone());

        // true = patient does not already exist
        // false = patient already exists
        return existingPatient == null;
    }

    // ---------------------------------------------------------
    // Validate Patient Login
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, String>> validatePatientLogin(
            Login login) {

        Map<String, String> response = new HashMap<>();

        try {

            Patient patient =
                    patientRepository.findByEmail(
                            login.getIdentifier());

            if (patient == null) {

                response.put(
                        "error",
                        "Invalid email or password");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            if (!patient.getPassword()
                    .equals(login.getPassword())) {

                response.put(
                        "error",
                        "Invalid email or password");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            String token =
                    tokenService.generateToken(
                            patient.getEmail());

            response.put("token", token);

            return ResponseEntity.ok(response);

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
    // Filter Patient Appointments
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition,
            String name,
            String token) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            // Extract patient ID from token
            String identifier =
                    tokenService.extractIdentifier(token);

            Patient patient =
                    patientRepository.findByEmail(identifier);

            if (patient == null) {

                response.put(
                        "error",
                        "Patient not found");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            Long patientId = patient.getId();

            boolean hasCondition =
                    condition != null
                    && !condition.isBlank();

            boolean hasDoctorName =
                    name != null
                    && !name.isBlank();

            // Condition + Doctor
            if (hasCondition && hasDoctorName) {

                return patientService
                        .filterByDoctorAndCondition(
                                condition,
                                name,
                                patientId);
            }

            // Condition only
            if (hasCondition) {

                return patientService
                        .filterByCondition(
                                condition,
                                patientId);
            }

            // Doctor only
            if (hasDoctorName) {

                return patientService
                        .filterByDoctor(
                                name,
                                patientId);
            }

            // No filters
            return patientService
                    .getPatientAppointment(
                            patientId,
                            token);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Unable to filter patient appointments");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
