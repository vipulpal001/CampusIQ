package com.campusiq.campusiq.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("CampusIQ - Email Verification OTP");

        message.setText(
                "Hello,\n\n" +
                "Your CampusIQ verification OTP is:\n\n" +
                otp + "\n\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "If you did not request this OTP, please ignore this email.\n\n" +
                "Regards,\n" +
                "CampusIQ Team"
        );

        mailSender.send(message);
    }
}