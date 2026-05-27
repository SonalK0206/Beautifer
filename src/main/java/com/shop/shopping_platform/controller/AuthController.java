package com.shop.shopping_platform.controller;

import com.shop.shopping_platform.model.User;
import com.shop.shopping_platform.service.OtpService;
import com.shop.shopping_platform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    // Show splash screen first
    @GetMapping("/")
    public String home() {
        return "splash";
    }

    // Show login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Handle login - send OTP
    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                               HttpSession session,
                               Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Email not registered. Please register first.");
            return "login";
        }
        try {
            otpService.generateAndSendOtp(email);
            session.setAttribute("otpEmail", email);
            return "redirect:/otp";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send OTP. Please try again.");
            return "login";
        }
    }

    // Show register page
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Handle registration
    @PostMapping("/register")
    public String handleRegister(@ModelAttribute User user, Model model) {
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "Email already registered. Please login.");
            return "register";
        }
        userService.registerUser(user);
        try {
            otpService.generateAndSendOtp(user.getEmail());
        } catch (Exception e) {
            model.addAttribute("error", "Registered but failed to send OTP. Please login.");
            return "login";
        }
        return "redirect:/otp";
    }

    // Show OTP page
    @GetMapping("/otp")
    public String otpPage(HttpSession session) {
        if (session.getAttribute("otpEmail") == null) {
            return "redirect:/login";
        }
        return "otp";
    }

    // Handle OTP verification
    @PostMapping("/otp")
    public String handleOtp(@RequestParam String otp,
                             HttpSession session,
                             Model model) {
        String email = (String) session.getAttribute("otpEmail");
        if (email == null) return "redirect:/login";

        if (otpService.validateOtp(email, otp)) {
            User user = userService.findByEmail(email);
            session.setAttribute("loggedInUser", user);
            session.removeAttribute("otpEmail");
            return "redirect:/products";
        } else {
            model.addAttribute("error", "Invalid or expired OTP. Please try again.");
            return "otp";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}