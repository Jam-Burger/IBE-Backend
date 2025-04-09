package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.EmailRequestDTO;
import com.kdu.hufflepuff.ibe.service.interfaces.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * Controller for handling email operations
 */
@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
@Tag(name = "Email API", description = "API endpoints for sending emails")
public class EmailController {

    private static final String DEFAULT_ATTACHMENT_NAME = "welcome-info.txt";
    private final EmailService emailService;

    /**
     * Sends an HTML email with attachment
     *
     * @param request Email information including recipient, subject, title and message
     * @return Success message or error details
     */
    @PostMapping("/send")
    @Operation(
            summary = "Send HTML email with attachment",
            description = "Sends an HTML formatted email with a predefined attachment to the specified recipient"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email sent successfully",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Failed to send email",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity<String> sendEmail(
            @Parameter(description = "Email request information", required = true)
            @Valid @RequestBody EmailRequestDTO request) {
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