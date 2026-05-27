package com.shop.shopping_platform.service;

import com.shop.shopping_platform.model.Cart;
import com.shop.shopping_platform.model.Product;
import com.shop.shopping_platform.model.User;
import com.shop.shopping_platform.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public List<Cart> getCartByUser(User user) {
        return cartRepository.findByUser(user);
    }

    public void addToCart(User user, Product product, int quantity) {
        // Check if product already in cart
        List<Cart> cartItems = cartRepository.findByUser(user);
        for (Cart item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                // Update quantity if already exists
                item.setQuantity(item.getQuantity() + quantity);
                cartRepository.save(item);
                return;
            }
        }
        // Add new cart item
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        cartRepository.save(cart);
    }

    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }

    public Double getCartTotal(List<Cart> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}