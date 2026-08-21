package com.campusiq.campusiq.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final EmailService emailService;

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();

    public OtpService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void generateAndSendOtp(String email) {

        String otp = String.format("%06d", new Random().nextInt(1_000_000));

        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        otpStorage.put(email, new OtpData(otp, expiryTime));

        emailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(String email, String enteredOtp) {

        OtpData otpData = otpStorage.get(email);

        if (otpData == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(otpData.expiryTime())) {
            otpStorage.remove(email);
            return false;
        }

        if (otpData.otp().equals(enteredOtp)) {
            otpStorage.remove(email);
            return true;
        }

        return false;
    }

    private record OtpData(String otp, LocalDateTime expiryTime) {
    }
}