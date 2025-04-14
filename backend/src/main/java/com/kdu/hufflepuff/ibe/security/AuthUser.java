package com.kdu.hufflepuff.ibe.security;

import com.kdu.hufflepuff.ibe.model.entity.Admin;
import com.kdu.hufflepuff.ibe.model.entity.Staff;
import com.kdu.hufflepuff.ibe.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class AuthUser implements UserDetails {
    private final String username;
    private final String password;
    private final Role role;

    public static AuthUser from(Staff staff) {
        return new AuthUser(staff.getStaffEmail(), staff.getStaffPassword(), Role.STAFF);
    }

    public static AuthUser from(Admin admin) {
        return new AuthUser(admin.getAdminEmail(), admin.getAdminPassword(), Role.ADMIN);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}