package com.project.back\_end.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back\_end.model.Admin;
import com.project.back\_end.model.Doctor;
import com.project.back\_end.model.Patient;
import com.project.back\_end.repository.AdminRepository;
import com.project.back\_end.repository.DoctorRepository;
import com.project.back\_end.repository.PatientRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenService {

```
private final AdminRepository adminRepository;
private final DoctorRepository doctorRepository;
private final PatientRepository patientRepository;

@Value("${jwt.secret}")
private String secret;

public TokenService(
        AdminRepository adminRepository,
        DoctorRepository doctorRepository,
        PatientRepository patientRepository) {

    this.adminRepository = adminRepository;
    this.doctorRepository = doctorRepository;
    this.patientRepository = patientRepository;
}

// ---------------------------------------------------------
// Generate JWT Token
// ---------------------------------------------------------
public String generateToken(String identifier) {

    Date now = new Date();

    // Token expires after 7 days
    Date expiryDate =
            new Date(now.getTime() + (7L * 24 * 60 * 60 * 1000));

    return Jwts.builder()
            .subject(identifier)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
}

// ---------------------------------------------------------
// Extract Identifier from JWT Token
// ---------------------------------------------------------
public String extractIdentifier(String token) {

    Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

    return claims.getSubject();
}

// ---------------------------------------------------------
// Validate JWT Token
// ---------------------------------------------------------
public boolean validateToken(String token, String user) {

    try {

        if (token == null || token.isBlank()) {
            return false;
        }

        // Extract username/email from JWT
        String identifier = extractIdentifier(token);

        if (identifier == null || identifier.isBlank()) {
            return false;
        }

        // Validate Admin
        if ("admin".equalsIgnoreCase(user)) {

            Admin admin =
                    adminRepository.findByUsername(identifier);

            return admin != null;
        }

        // Validate Doctor
        if ("doctor".equalsIgnoreCase(user)) {

            Doctor doctor =
                    doctorRepository.findByEmail(identifier);

            return doctor != null;
        }

        // Validate Patient
        if ("patient".equalsIgnoreCase(user)
                || "loggedPatient".equalsIgnoreCase(user)) {

            Patient patient =
                    patientRepository.findByEmail(identifier);

            return patient != null;
        }

        return false;

    } catch (Exception e) {

        // Handles:
        // - expired token
        // - invalid signature
        // - malformed token
        // - invalid JWT
        return false;
    }
}

// ---------------------------------------------------------
// Get JWT Signing Key
// ---------------------------------------------------------
private SecretKey getSigningKey() {

    return Keys.hmacShaKeyFor(
            secret.getBytes(StandardCharsets.UTF_8)
    );
}
```

}
