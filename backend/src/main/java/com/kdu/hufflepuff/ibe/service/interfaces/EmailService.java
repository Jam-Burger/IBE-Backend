package com.kdu.hufflepuff.ibe.service.interfaces;

import jakarta.mail.MessagingException;
import org.thymeleaf.context.Context;

public interface EmailService {
    void sendEmailWithAttachment(String to,
                                 String subject,
                                 String templateName,
                                 Context context,
                                 String attachmentPath,
                                 String attachmentName) throws MessagingException;

    void sendEmail(String to, String subject, String templateName, Context context) throws MessagingException;
} 