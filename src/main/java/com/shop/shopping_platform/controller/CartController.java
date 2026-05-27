package com.shop.shopping_platform.controller;

import com.shop.shopping_platform.model.Cart;
import com.shop.shopping_platform.model.Product;
import com.shop.shopping_platform.model.User;
import com.shop.shopping_platform.service.CartService;
import com.shop.shopping_platform.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    // View cart
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("loggedInUser");
        List<Cart> cartItems = cartService.getCartByUser(user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", cartService.getCartTotal(cartItems));
        model.addAttribute("user", user);
        return "cart";
    }

    // Add to cart
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                             @RequestParam(defaultValue = "1") int quantity,
                             HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("loggedInUser");
        Product product = productService.getProductById(productId);
        if (product != null) {
            cartService.addToCart(user, product, quantity);
        }
        return "redirect:/products";
    }

    // Remove from cart
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long cartId,
                                  HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        cartService.removeFromCart(cartId);
        return "redirect:/cart";
    }
}