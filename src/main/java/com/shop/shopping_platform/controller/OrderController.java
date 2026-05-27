package com.shop.shopping_platform.controller;

import com.shop.shopping_platform.model.Cart;
import com.shop.shopping_platform.model.Order;
import com.shop.shopping_platform.model.User;
import com.shop.shopping_platform.service.CartService;
import com.shop.shopping_platform.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    // Place order
    @PostMapping("/place")
    public String placeOrder(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("loggedInUser");
        List<Cart> cartItems = cartService.getCartByUser(user);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        Order order = orderService.placeOrder(user, cartItems);
        model.addAttribute("order", order);
        model.addAttribute("user", user);
        return "orderconfirmation";
    }

    // View order history
    @GetMapping("/history")
    public String orderHistory(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("orders", orderService.getOrdersByUser(user));
        model.addAttribute("user", user);
        return "orderhistory";
    }
}