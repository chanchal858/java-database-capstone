package com.project.back_end.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.back_end.models.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find a patient by email
    Patient findByEmail(String email);

    // Find a patient by either email or phone
    Patient findByEmailOrPhone(String email, String phone);
}
