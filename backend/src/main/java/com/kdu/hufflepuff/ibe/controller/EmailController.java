package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.EmailRequestDTO;
import com.kdu.hufflepuff.ibe.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private static final String DEFAULT_ATTACHMENT_NAME = "welcome-info.txt";
    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequestDTO request) {
        try {
            Context context = new Context();
            context.setVariable("title", request.getTitle());
            context.setVariable("message", request.getMessage());

            String attachmentPath = new ClassPathResource("attachments/sample.txt").getFile().getAbsolutePath();

            emailService.sendHtmlEmailWithAttachment(
                request.getTo(),
                request.getSubject(),
                "email-template",
                context,
                attachmentPath,
                DEFAULT_ATTACHMENT_NAME
            );

            return ResponseEntity.ok("Email sent successfully");
        } catch (MessagingException | IOException e) {
            return ResponseEntity.internalServerError()
                .body("Failed to send email: " + e.getMessage());
        }
    }
} 