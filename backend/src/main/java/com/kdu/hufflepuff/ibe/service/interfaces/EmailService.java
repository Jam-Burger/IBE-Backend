package com.kdu.hufflepuff.ibe.service.interfaces;

import jakarta.mail.MessagingException;
import org.thymeleaf.context.Context;

public interface EmailService {
    void sendHtmlEmailWithAttachment(String to,
                                     String subject,
                                     String templateName,
                                     Context context,
                                     String attachmentPath,
                                     String attachmentName) throws MessagingException;
} 