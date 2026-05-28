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

    @Transactional
    public void generateAndSendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        otpRepository.findByEmail(email).ifPresent(otpRepository::delete);

        OtpStore otpStore = new OtpStore();
        otpStore.setEmail(email);
        otpStore.setOtp(otp);
        otpStore.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otpStore);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply.onetimepwd@gmail.com");
            message.setTo(email);
            message.setSubject("Your Login OTP - Shopping Platform");
            message.setText("Your OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes.");
            mailSender.send(message);
            System.out.println("OTP email sent successfully to: " + email);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
    }

    @Transactional
    public boolean validateOtp(String email, String otp) {
        OtpStore otpStore = otpRepository.findByEmail(email).orElse(null);
        if (otpStore == null) return false;
        if (LocalDateTime.now().isAfter(otpStore.getExpiresAt())) return false;
        if (!otpStore.getOtp().equals(otp)) return false;
        otpRepository.deleteByEmail(email);
        return true;
    }
}