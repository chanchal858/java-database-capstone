package com.project.back_end.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final Service service;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService,
            Service service) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    // ---------------------------------------------------------
    // Book Appointment
    // ---------------------------------------------------------

    public int bookAppointment(Appointment appointment) {

        try {

            if (appointment == null) {
                return 0;
            }

            appointmentRepository.save(appointment);

            return 1;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }

    // ---------------------------------------------------------
    // Update Appointment
    // ---------------------------------------------------------

    public ResponseEntity<Map<String, String>> updateAppointment(
            Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        try {

            if (appointment == null || appointment.getId() == null) {

                response.put("error", "Invalid appointment");

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(response);
            }

            Optional<Appointment> existingAppointment =
                    appointmentRepository.findById(
                            appointment.getId());

            if (existingAppointment.isEmpty()) {

                response.put(
                        "error",
                        "Appointment not found");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

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

            appointmentRepository.save(appointment);

            response.put(
                    "message",
                    "Appointment updated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Failed to update appointment");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Cancel Appointment
    // ---------------------------------------------------------

    public ResponseEntity<Map<String, String>> cancelAppointment(
            long id,
            String token) {

        Map<String, String> response = new HashMap<>();

        try {

            if (!tokenService.validateToken(token, "patient")) {

                response.put(
                        "error",
                        "Invalid or expired token");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            Optional<Appointment> optionalAppointment =
                    appointmentRepository.findById(id);

            if (optionalAppointment.isEmpty()) {

                response.put(
                        "error",
                        "Appointment not found");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            Appointment appointment =
                    optionalAppointment.get();

            String identifier =
                    tokenService.extractIdentifier(token);

            Patient patient =
                    patientRepository.findByEmail(identifier);

            if (patient == null
                    || appointment.getPatient() == null
                    || !appointment.getPatient()
                            .getId()
                            .equals(patient.getId())) {

                response.put(
                        "error",
                        "You are not authorized to cancel this appointment");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            appointmentRepository.delete(appointment);

            response.put(
                    "message",
                    "Appointment cancelled successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Failed to cancel appointment");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // ---------------------------------------------------------
    // Get Appointments For Doctor
    // ---------------------------------------------------------

    public Map<String, Object> getAppointment(
            String pname,
            LocalDate date,
            String token) {

        Map<String, Object> response = new HashMap<>();

        try {

            if (!tokenService.validateToken(token, "doctor")) {

                response.put(
                        "error",
                        "Invalid or expired token");

                return response;
            }

            String identifier =
                    tokenService.extractIdentifier(token);

            Doctor doctor =
                    doctorRepository.findByEmail(identifier);

            if (doctor == null) {

                response.put(
                        "error",
                        "Doctor not found");

                return response;
            }

            if (date == null) {
                date = LocalDate.now();
            }

            LocalDateTime start =
                    date.atStartOfDay();

            LocalDateTime end =
                    date.plusDays(1).atStartOfDay();

            List<Appointment> appointments;

            if (pname == null
                    || pname.isBlank()
                    || "null".equalsIgnoreCase(pname)) {

                appointments =
                        appointmentRepository
                                .findByDoctorIdAndAppointmentTimeBetween(
                                        doctor.getId(),
                                        start,
                                        end);

            } else {

                appointments =
                        appointmentRepository
                                .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                        doctor.getId(),
                                        pname,
                                        start,
                                        end);
            }

            response.put("appointments", appointments);

            return response;

        } catch (Exception e) {

            e.printStackTrace();

            response.put(
                    "error",
                    "Failed to retrieve appointments");

            return response;
        }
    }
}
