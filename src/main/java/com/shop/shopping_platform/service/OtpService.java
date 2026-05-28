package com.shop.shopping_platform.service;

import com.shop.shopping_platform.model.OtpStore;
import com.shop.shopping_platform.Repository.OtpRepository;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

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
            Email from = new Email("noreply.onetimepwd@gmail.com");
            Email to = new Email(email);
            Content content = new Content("text/plain",
                "Your OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes.");
            Mail mail = new Mail(from, "Your Login OTP - Shopping Platform", to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("SendGrid status: " + response.getStatusCode());
            System.out.println("SendGrid body: " + response.getBody());
        } catch (IOException e) {
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