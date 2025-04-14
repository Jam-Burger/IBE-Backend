package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.entity.Admin;
import com.kdu.hufflepuff.ibe.model.entity.Staff;
import com.kdu.hufflepuff.ibe.repository.jpa.AdminRepository;
import com.kdu.hufflepuff.ibe.repository.jpa.StaffRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StaffRepository staffRepository;
    private final AdminRepository adminRepository;

    public CustomUserDetailsService(StaffRepository staffRepository, AdminRepository adminRepository) {
        this.staffRepository = staffRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First check if username belongs to an admin
        Optional<Admin> adminOpt = adminRepository.findByAdminEmail(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return new User(
                    admin.getAdminName(),
                    admin.getAdminPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // Then check if username belongs to a staff
        Optional<Staff> staffOpt = staffRepository.findByStaffName(username);
        if (staffOpt.isPresent()) {
            Staff staff = staffOpt.get();
            return new User(
                    staff.getStaffEmail(),
                    staff.getStaffPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_STAFF"))
            );
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    /**
     * Get the ID associated with a username, based on whether it's an admin or staff
     * @param username The username to look up
     * @return The ID of the admin or staff, or null if not found
     */
    public Long getUserIdFromUsername(String username) {
        // First check admins
        Optional<Admin> adminOpt = adminRepository.findByAdminEmail(username);
        if (adminOpt.isPresent()) {
            return adminOpt.get().getId();
        }

        // Then check staff
        Optional<Staff> staffOpt = staffRepository.findByStaffName(username);
        if (staffOpt.isPresent()) {
            return staffOpt.get().getId();
        }

        return null;
    }

    /**
     * Check if the user with the given username is an admin
     * @param username The username to check
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin(String username) {
        return adminRepository.findByAdminEmail(username).isPresent();
    }

    /**
     * Check if the user with the given username is a staff member
     * @param username The username to check
     * @return true if the user is a staff member, false otherwise
     */
    public boolean isStaff(String username) {
        return staffRepository.findByStaffName(username).isPresent();
    }

    /**
     * Get the staff ID associated with a username
     * @param username The username to look up
     * @return The ID of the staff, or null if not a staff member
     */
    public Long getStaffIdFromUsername(String username) {
        Optional<Staff> staffOpt = staffRepository.findByStaffName(username);
        return staffOpt.map(Staff::getId).orElse(null);
    }
}
