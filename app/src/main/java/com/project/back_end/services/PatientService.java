package com.project.back_end.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.dto.AppointmentDTO;
import com.project.back_end.model.Appointment;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {

        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // ---------------------------------------------------------
    // Create Patient
    // ---------------------------------------------------------

    public int createPatient(Patient patient) {

        try {

            patientRepository.save(patient);

            return 1;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }

    // ---------------------------------------------------------
    // Get Patient Appointments
    // ---------------------------------------------------------

    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            Long id,
            String token) {

        Map<String, Object> response = new HashMap<>();

        try {

            if (token == null || token.isBlank()) {

                response.put("error", "Token is missing");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            if (!tokenService.validateToken(token, "patient")) {

                response.put(
                        "error",
                        "Invalid or expired token");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            String identifier =
                    tokenService.extractIdentifier(token);

            Patient patient =
                    patientRepository.findByEmail(identifier);

            if (patient == null) {

                response.put(
                        "error",
                        "Patient not found");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            // Make sure the patient can access only
            // their own appointments.
            if (!patient.getId().equals(id)) {

                response.put(
                        "error",
                        "Unauthorized access");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .findByPatientId(id);

            List<AppointmentDTO> appointmentDTOs =
                    convertToDTO(appointments);

            response.put(
                    "appointments",
                    appointmentDTOs);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Unable to retrieve appointments");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Filter Appointments By Condition
    // ---------------------------------------------------------

    public ResponseEntity<Map<String, Object>> filterByCondition(
            String condition,
            Long id) {

        Map<String, Object> response = new HashMap<>();

        try {

            if (condition == null
                    || condition.isBlank()) {

                response.put(
                        "error",
                        "Condition is required");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            List<Appointment> appointments;

            if ("past".equalsIgnoreCase(condition)) {

                /*
                 * According to the project instructions:
                 * status 1 = past/completed
                 */
                appointments =
                        appointmentRepository
                                .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                                        id,
                                        1);

            } else if ("future".equalsIgnoreCase(condition)) {

                /*
                 * According to the project instructions:
                 * status 0 = future/scheduled
                 */
                appointments =
                        appointmentRepository
                                .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                                        id,
                                        0);

            } else {

                response.put(
                        "error",
                        "Invalid condition. Use past or future");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            List<AppointmentDTO> appointmentDTOs =
                    convertToDTO(appointments);

            response.put(
                    "appointments",
                    appointmentDTOs);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Unable to filter appointments");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Filter By Doctor
    // ---------------------------------------------------------

    public ResponseEntity<Map<String, Object>> filterByDoctor(
            String name,
            Long patientId) {

        Map<String, Object> response = new HashMap<>();

        try {

            if (name == null || name.isBlank()) {

                response.put(
                        "error",
                        "Doctor name is required");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .filterByDoctorNameAndPatientId(
                                    name,
                                    patientId);

            List<AppointmentDTO> appointmentDTOs =
                    convertToDTO(appointments);

            response.put(
                    "appointments",
                    appointmentDTOs);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Unable to filter appointments by doctor");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Filter By Doctor + Condition
    // ---------------------------------------------------------

    public ResponseEntity<Map<String, Object>>
            filterByDoctorAndCondition(
                    String condition,
                    String name,
                    long patientId) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            if (condition == null
                    || condition.isBlank()) {

                response.put(
                        "error",
                        "Condition is required");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            if (name == null || name.isBlank()) {

                response.put(
                        "error",
                        "Doctor name is required");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            int status;

            if ("past".equalsIgnoreCase(condition)) {

                status = 1;

            } else if ("future".equalsIgnoreCase(condition)) {

                status = 0;

            } else {

                response.put(
                        "error",
                        "Invalid condition. Use past or future");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .filterByDoctorNameAndPatientIdAndStatus(
                                    name,
                                    patientId,
                                    status);

            List<AppointmentDTO> appointmentDTOs =
                    convertToDTO(appointments);

            response.put(
                    "appointments",
                    appointmentDTOs);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Unable to filter appointments");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Get Patient Details
    // ---------------------------------------------------------

    public ResponseEntity<Map<String, Object>> getPatientDetails(
            String token) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            if (token == null || token.isBlank()) {

                response.put(
                        "error",
                        "Token is missing");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            if (!tokenService.validateToken(
                    token,
                    "patient")) {

                response.put(
                        "error",
                        "Invalid or expired token");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            String identifier =
                    tokenService.extractIdentifier(token);

            Patient patient =
                    patientRepository.findByEmail(identifier);

            if (patient == null) {

                response.put(
                        "error",
                        "Patient not found");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            response.put("patient", patient);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Unable to retrieve patient details");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Convert Appointment Entity -> AppointmentDTO
    // ---------------------------------------------------------

    private List<AppointmentDTO> convertToDTO(
            List<Appointment> appointments) {

        List<AppointmentDTO> appointmentDTOs =
                new ArrayList<>();

        if (appointments == null) {
            return appointmentDTOs;
        }

        for (Appointment appointment : appointments) {

            if (appointment == null
                    || appointment.getAppointmentTime() == null) {

                continue;
            }

            Long doctorId = null;
            String doctorName = null;

            if (appointment.getDoctor() != null) {

                doctorId =
                        appointment.getDoctor().getId();

                doctorName =
                        appointment.getDoctor().getName();
            }

            Long patientId = null;
            String patientName = null;
            String patientEmail = null;
            String patientPhone = null;
            String patientAddress = null;

            if (appointment.getPatient() != null) {

                patientId =
                        appointment.getPatient().getId();

                patientName =
                        appointment.getPatient().getName();

                patientEmail =
                        appointment.getPatient().getEmail();

                patientPhone =
                        appointment.getPatient().getPhone();

                patientAddress =
                        appointment.getPatient().getAddress();
            }

            AppointmentDTO dto =
                    new AppointmentDTO(
                            appointment.getId(),
                            doctorId,
                            doctorName,
                            patientId,
                            patientName,
                            patientEmail,
                            patientPhone,
                            patientAddress,
                            appointment.getAppointmentTime(),
                            appointment.getStatus());

            appointmentDTOs.add(dto);
        }

        return appointmentDTOs;
    }
}
