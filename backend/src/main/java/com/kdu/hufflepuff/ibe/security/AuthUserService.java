package com.kdu.hufflepuff.ibe.security;


import com.kdu.hufflepuff.ibe.model.dto.out.UserWithTokenResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.Admin;
import com.kdu.hufflepuff.ibe.model.entity.Staff;
import com.kdu.hufflepuff.ibe.service.interfaces.AdminService;
import com.kdu.hufflepuff.ibe.service.interfaces.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserService implements UserDetailsService {
    private final StaffService staffService;
    private final AdminService adminService;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return findByEmail(email);
    }

    public UserDetails findByEmail(String email) {
        if (isAdmin(email)) {
            Admin admin = adminService.findByEmail(email);
            return AuthUser.from(admin);
        }
        if (isStaff(email)) {
            Staff staff = staffService.findByEmail(email);
            return AuthUser.from(staff);
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    public UserWithTokenResponseDTO login(Authentication authentication) {
        return generateUserWithToken((UserDetails) authentication.getPrincipal());
    }

    public UserWithTokenResponseDTO generateUserWithToken(UserDetails userDetails) {
        String token = jwtService.generateToken(userDetails);
        if (isAdmin(userDetails.getUsername())) {
            Admin admin = adminService.findByEmail(userDetails.getUsername());
            return UserWithTokenResponseDTO.builder()
                .name(admin.getAdminName())
                .email(admin.getAdminEmail())
                .token(token)
                .build();
        }

        if (isStaff(userDetails.getUsername())) {
            Staff staff = staffService.findByEmail(userDetails.getUsername());
            return UserWithTokenResponseDTO.builder()
                .name(staff.getStaffName())
                .email(staff.getStaffEmail())
                .token(token)
                .build();
        }

        throw new UsernameNotFoundException("User not found with email: " + userDetails.getUsername());
    }

    public boolean isAdmin(String email) {
        return adminService.existsByEmail(email);
    }

    public boolean isStaff(String email) {
        return staffService.existsByEmail(email);
    }
}

