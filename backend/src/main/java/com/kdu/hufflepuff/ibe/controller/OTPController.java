package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.service.interfaces.OTPService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
@Slf4j
public class OTPController {
    private OTPService otpService;

    @Autowired
    public OTPController(OTPService otpService){
        this.otpService=otpService;
    }


    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        log.info("----------------Sending OTP to email: {}----------------", email);
        otpService.generateOtp(email);
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);
        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }
}
