package com.shop.shopping_platform.service;

import com.shop.shopping_platform.model.OtpStore;
import com.shop.shopping_platform.Repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    // Generate and send OTP
    public void generateAndSendOtp(String email) {
        // Generate 6 digit OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        // Delete old OTP if exists
        otpRepository.findByEmail(email).ifPresent(otpRepository::delete);

        // Save new OTP with 5 min expiry
        OtpStore otpStore = new OtpStore();
        otpStore.setEmail(email);
        otpStore.setOtp(otp);
        otpStore.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otpStore);

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Login OTP - Shopping Platform");
        message.setText("Your OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes.");
        mailSender.send(message);
    }

    // Validate OTP
    @Transactional
    public boolean validateOtp(String email, String otp) {
        OtpStore otpStore = otpRepository.findByEmail(email).orElse(null);

        if (otpStore == null) return false;
        if (LocalDateTime.now().isAfter(otpStore.getExpiresAt())) return false;
        if (!otpStore.getOtp().equals(otp)) return false;

        // OTP is valid - delete it so it can't be reused
        otpRepository.deleteByEmail(email);
        return true;
    }
}