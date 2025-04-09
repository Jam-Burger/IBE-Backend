package com.kdu.hufflepuff.ibe.model.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request object for sending emails")
public class EmailRequestDTO {
    @NotBlank(message = "Email recipient is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email recipient address", example = "user@example.com")
    private String to;

    @NotBlank(message = "Subject is required")
    @Schema(description = "Email subject line", example = "Welcome to our service")
    private String subject;

    @NotBlank(message = "Title is required")
    @Schema(description = "Title shown in the email body", example = "Welcome aboard!")
    private String title;

    @NotBlank(message = "Message is required")
    @Schema(description = "Main message content of the email",
            example = "Thank you for joining our platform. We're excited to have you!")
    private String message;
}