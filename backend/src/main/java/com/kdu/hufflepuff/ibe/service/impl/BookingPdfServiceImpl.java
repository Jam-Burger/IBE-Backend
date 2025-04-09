package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.BookingOperationException;
import com.kdu.hufflepuff.ibe.exception.MiscellaneousException;
import com.kdu.hufflepuff.ibe.model.dto.out.BookingDetailsDTO;
import com.kdu.hufflepuff.ibe.service.interfaces.BookingPdfService;
import com.kdu.hufflepuff.ibe.service.interfaces.BookingService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class BookingPdfServiceImpl implements BookingPdfService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final BookingService bookingService;

    @Autowired
    public BookingPdfServiceImpl(TemplateEngine templateEngine, JavaMailSender mailSender, BookingService bookingService) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.bookingService = bookingService;
    }

    public void generateAndSendBookingPdf(Long bookingId) {
        BookingDetailsDTO booking = bookingService.getBookingDetailsById(bookingId);
        if (booking == null) throw BookingOperationException.bookingNotFound(bookingId);

        // Fill HTML for PDF
        Context context = new Context();
        context.setVariable("booking", booking);
        String pdfHtmlContent = templateEngine.process("booking-report", context);
        byte[] pdfBytes = renderPdfFromHtml(pdfHtmlContent);

        // Fill HTML for email body
        String emailHtmlContent = templateEngine.process("booking-email", context); // assumes booking-email.html in templates

        // Send email with attachment
        sendEmailWithAttachment(booking.getGuestDetails().getTravelerEmail(), emailHtmlContent, pdfBytes, bookingId);
    }

    private byte[] renderPdfFromHtml(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error while generating PDF: ", e);
            throw new MiscellaneousException("Failed to generate PDF");
        }
    }

    private void sendEmailWithAttachment(String to, String htmlBody, byte[] pdfBytes, Long bookingId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject("Booking Confirmation - #" + bookingId);
            helper.setText(htmlBody, true); // true enables HTML

            helper.addAttachment("booking-" + bookingId + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error while sending email: ", e);
            throw new MiscellaneousException("Failed to send email");
        }
    }
}
