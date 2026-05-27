package com.shop.shopping_platform.controller;

import com.shop.shopping_platform.model.Product;
import com.shop.shopping_platform.model.User;
import com.shop.shopping_platform.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String productsPage(
            @RequestParam(required = false, defaultValue = "") String search,
            HttpSession session, Model model) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        List<Product> products = productService.getAllProducts();

        // Filter by search keyword if provided
        if (!search.isEmpty()) {
            String keyword = search.toLowerCase();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(keyword)
                            || p.getDescription().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }

        model.addAttribute("products", products);
        model.addAttribute("search", search);
        model.addAttribute("user", (User) session.getAttribute("loggedInUser"));
        return "products";
    }
}