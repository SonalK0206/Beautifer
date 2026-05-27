package com.shop.shopping_platform.controller;

import com.shop.shopping_platform.model.User;
import com.shop.shopping_platform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    // View profile
    @GetMapping
    public String profilePage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", (User) session.getAttribute("loggedInUser"));
        return "profile";
    }

    // Update profile
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User updatedUser,
                                 HttpSession session,
                                 Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        User existingUser = (User) session.getAttribute("loggedInUser");
        existingUser.setName(updatedUser.getName());
        existingUser.setMobile(updatedUser.getMobile());
        existingUser.setAddress(updatedUser.getAddress());
        userService.updateUser(existingUser);

        // Update session with new details
        session.setAttribute("loggedInUser", existingUser);
        model.addAttribute("user", existingUser);
        model.addAttribute("success", "Profile updated successfully!");
        return "profile";
    }
}