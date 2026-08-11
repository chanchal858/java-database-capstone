package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {

        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // ---------------------------------------------------------
    // Get Doctor Availability
    // ---------------------------------------------------------

    public List<String> getDoctorAvailability(
            Long doctorId,
            LocalDate date) {

        List<String> availableSlots = new ArrayList<>();

        try {

            Doctor doctor = doctorRepository
                    .findById(doctorId)
                    .orElse(null);

            if (doctor == null
                    || doctor.getAvailableTimes() == null) {

                return availableSlots;
            }

            LocalDateTime start =
                    date.atStartOfDay();

            LocalDateTime end =
                    date.plusDays(1).atStartOfDay();

            List<Appointment> appointments =
                    appointmentRepository
                            .findByDoctorIdAndAppointmentTimeBetween(
                                    doctorId,
                                    start,
                                    end);

            List<String> bookedSlots = new ArrayList<>();

            for (Appointment appointment : appointments) {

                if (appointment.getAppointmentTime() != null) {

                    LocalTime time =
                            appointment.getAppointmentTime()
                                    .toLocalTime();

                    bookedSlots.add(
                            time.toString().substring(0, 5));
                }
            }

            for (String slot : doctor.getAvailableTimes()) {

                if (slot == null || slot.isBlank()) {
                    continue;
                }

                String normalizedSlot = slot.trim();

                if (!bookedSlots.contains(normalizedSlot)) {
                    availableSlots.add(normalizedSlot);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return availableSlots;
    }

    // ---------------------------------------------------------
    // Save Doctor
    // ---------------------------------------------------------

    public int saveDoctor(Doctor doctor) {

        try {

            Doctor existingDoctor =
                    doctorRepository.findByEmail(
                            doctor.getEmail());

            if (existingDoctor != null) {
                return -1;
            }

            doctorRepository.save(doctor);

            return 1;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }

    // ---------------------------------------------------------
    // Update Doctor
    // ---------------------------------------------------------

    public int updateDoctor(Doctor doctor) {

        try {

            if (doctor == null
                    || doctor.getId() == null) {

                return -1;
            }

            if (!doctorRepository.existsById(
                    doctor.getId())) {

                return -1;
            }

            doctorRepository.save(doctor);

            return 1;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }

    // ---------------------------------------------------------
    // Get All Doctors
    // ---------------------------------------------------------

    public List<Doctor> getDoctors() {

        return doctorRepository.findAll();
    }

    // ---------------------------------------------------------
    // Delete Doctor
    // ---------------------------------------------------------

    public int deleteDoctor(long id) {

        try {

            if (!doctorRepository.existsById(id)) {
                return -1;
            }

            // Delete appointments first
            appointmentRepository
                    .deleteAllByDoctorId(id);

            // Then delete doctor
            doctorRepository.deleteById(id);

            return 1;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }

    // ---------------------------------------------------------
    // Validate Doctor Login
    // ---------------------------------------------------------

    public ResponseEntity<Map<String, String>> validateDoctor(
            Login login) {

        Map<String, String> response =
                new HashMap<>();

        try {

            Doctor doctor =
                    doctorRepository.findByEmail(
                            login.getIdentifier());

            if (doctor == null) {

                response.put(
                        "error",
                        "Invalid email or password");

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            if (!doctor.getPassword()
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
                            doctor.getEmail());

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
    // Find Doctor By Name
    // ---------------------------------------------------------

    public Map<String, Object> findDoctorByName(
            String name) {

        Map<String, Object> response =
                new HashMap<>();

        List<Doctor> doctors =
                doctorRepository.findByNameLike(name);

        response.put("doctors", doctors);

        return response;
    }

    // ---------------------------------------------------------
    // Name + Specialty + Time
    // ---------------------------------------------------------

    public Map<String, Object>
    filterDoctorsByNameSpecilityandTime(
            String name,
            String specialty,
            String amOrPm) {

        List<Doctor> doctors =
                doctorRepository
                        .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                                name,
                                specialty);

        doctors =
                filterDoctorByTime(
                        doctors,
                        amOrPm);

        Map<String, Object> response =
                new HashMap<>();

        response.put("doctors", doctors);

        return response;
    }

    // ---------------------------------------------------------
    // Name + Time
    // ---------------------------------------------------------

    public Map<String, Object>
    filterDoctorByNameAndTime(
            String name,
            String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findByNameLike(name);

        doctors =
                filterDoctorByTime(
                        doctors,
                        amOrPm);

        Map<String, Object> response =
                new HashMap<>();

        response.put("doctors", doctors);

        return response;
    }

    // ---------------------------------------------------------
    // Name + Specialty
    // ---------------------------------------------------------

    public Map<String, Object>
    filterDoctorByNameSpecility(
            String name,
            String specilty) {

        Map<String, Object> response =
                new HashMap<>();

        List<Doctor> doctors =
                doctorRepository
                        .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                                name,
                                specilty);

        response.put("doctors", doctors);

        return response;
    }

    // ---------------------------------------------------------
    // Specialty + Time
    // ---------------------------------------------------------

    public Map<String, Object>
    filterDoctorByTimeAndSpecility(
            String specilty,
            String amOrPm) {

        List<Doctor> doctors =
                doctorRepository
                        .findBySpecialtyIgnoreCase(
                                specilty);

        doctors =
                filterDoctorByTime(
                        doctors,
                        amOrPm);

        Map<String, Object> response =
                new HashMap<>();

        response.put("doctors", doctors);

        return response;
    }

    // ---------------------------------------------------------
    // Specialty Only
    // ---------------------------------------------------------

    public Map<String, Object>
    filterDoctorBySpecility(
            String specilty) {

        Map<String, Object> response =
                new HashMap<>();

        List<Doctor> doctors =
                doctorRepository
                        .findBySpecialtyIgnoreCase(
                                specilty);

        response.put("doctors", doctors);

        return response;
    }

    // ---------------------------------------------------------
    // Time Only
    // ---------------------------------------------------------

    public Map<String, Object>
    filterDoctorsByTime(
            String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findAll();

        doctors =
                filterDoctorByTime(
                        doctors,
                        amOrPm);

        Map<String, Object> response =
                new HashMap<>();

        response.put("doctors", doctors);

        return response;
    }

    // ---------------------------------------------------------
    // Private Time Filter
    // ---------------------------------------------------------

    private List<Doctor> filterDoctorByTime(
            List<Doctor> doctors,
            String amOrPm) {

        List<Doctor> filteredDoctors =
                new ArrayList<>();

        if (amOrPm == null
                || amOrPm.isBlank()) {

            return doctors;
        }

        String requestedPeriod =
                amOrPm.trim().toUpperCase();

        for (Doctor doctor : doctors) {

            if (doctor.getAvailableTimes() == null) {
                continue;
            }

            boolean matched = false;

            for (String slot :
                    doctor.getAvailableTimes()) {

                if (slot == null || slot.isBlank()) {
                    continue;
                }

                String timeSlot =
                        slot.trim().toUpperCase();

                if (containsRequestedPeriod(
                        timeSlot,
                        requestedPeriod)) {

                    matched = true;
                    break;
                }
            }

            if (matched) {
                filteredDoctors.add(doctor);
            }
        }

        return filteredDoctors;
    }

    // ---------------------------------------------------------
    // Check AM / PM
    // ---------------------------------------------------------

    private boolean containsRequestedPeriod(
            String timeSlot,
            String requestedPeriod) {

        /*
         * Handles values such as:
         *
         * 09:00 AM
         * 10:30 AM
         * 02:00 PM
         * 14:00
         * 09:00
         */

        if (timeSlot.contains(requestedPeriod)) {
            return true;
        }

        try {

            String timePart =
                    timeSlot
                            .replace("AM", "")
                            .replace("PM", "")
                            .trim();

            LocalTime time;

            if (timePart.length() >= 5) {

                time = LocalTime.parse(
                        timePart.substring(0, 5));
            } else {
                return false;
            }

            if ("AM".equals(requestedPeriod)) {
                return time.getHour() < 12;
            }

            if ("PM".equals(requestedPeriod)) {
                return time.getHour() >= 12;
            }

        } catch (Exception e) {

            // Ignore invalid time format
        }

        return false;
    }
}
