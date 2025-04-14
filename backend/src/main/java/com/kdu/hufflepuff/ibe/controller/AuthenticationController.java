package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.config.JwtUtil;
import com.kdu.hufflepuff.ibe.model.dto.in.AuthenticationRequest;
import com.kdu.hufflepuff.ibe.model.dto.in.AuthenticationResponse;
import com.kdu.hufflepuff.ibe.model.entity.Admin;
import com.kdu.hufflepuff.ibe.model.entity.Staff;
import com.kdu.hufflepuff.ibe.repository.jpa.AdminRepository;
import com.kdu.hufflepuff.ibe.repository.jpa.StaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AdminRepository adminRepository;
    private final StaffRepository staffRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationController(
            AdminRepository adminRepository,
            StaffRepository staffRepository,
            JwtUtil jwtUtil) {
        this.adminRepository = adminRepository;
        this.staffRepository = staffRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        if ("ADMIN".equalsIgnoreCase(request.getUserType())) {
            // Try to authenticate admin by email/password
            Optional<Admin> adminOptional = adminRepository.findByAdminEmailAndAdminPassword(
                    request.getUsername(), request.getPassword());

            if (!adminOptional.isPresent()) {
                // Try by name/password
                adminOptional = adminRepository.findByAdminNameAndAdminPassword(
                        request.getUsername(), request.getPassword());
            }

            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                String token = jwtUtil.generateToken(admin.getAdminName(), "ROLE_ADMIN", admin.getId());

                return ResponseEntity.ok(new AuthenticationResponse(
                        token, "ADMIN", admin.getId(), admin.getAdminName()));
            }
        } else if ("STAFF".equalsIgnoreCase(request.getUserType())) {
            // Try to authenticate staff by email/password
            Optional<Staff> staffOptional = staffRepository.findByStaffEmailAndStaffPassword(
                    request.getUsername(), request.getPassword());

            if (!staffOptional.isPresent()) {
                // Try by name/password
                staffOptional = staffRepository.findByStaffNameAndStaffPassword(
                        request.getUsername(), request.getPassword());
            }

            if (staffOptional.isPresent()) {
                Staff staff = staffOptional.get();
                String token = jwtUtil.generateToken(staff.getStaffName(), "ROLE_STAFF", staff.getId());

                return ResponseEntity.ok(new AuthenticationResponse(
                        token, "STAFF", staff.getId(), staff.getStaffName()));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid credentials");
    }
}
