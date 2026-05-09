package edu.cit.esparcia.barangayio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Barangay.io Online Appointment System");
            message.setText("Dear " + firstName + ",\n\n"
                    + "Welcome to the Barangay Online Appointment and Certification Management System (BOACMS).\n"
                    + "Your account has been successfully registered.\n\n"
                    + "You can now book appointments and request certificates online.\n\n"
                    + "Best regards,\nBarangay Admin");
            mailSender.send(message);
            log.info("Welcome email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}", toEmail, e);
        }
    }

    public void sendAppointmentNotification(String toEmail, String status, String date, String time) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Appointment Status Update: " + status);
            message.setText("Dear Resident,\n\n"
                    + "Your appointment scheduled for " + date + " at " + time + " is now marked as: " + status + ".\n\n"
                    + "Please check your dashboard for more details.\n\n"
                    + "Best regards,\nBarangay Admin");
            mailSender.send(message);
            log.info("Appointment notification email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send appointment email to {}", toEmail, e);
        }
    }
}
