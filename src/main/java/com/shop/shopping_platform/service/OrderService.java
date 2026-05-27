package com.shop.shopping_platform.service;

import com.shop.shopping_platform.model.Cart;
import com.shop.shopping_platform.model.Order;
import com.shop.shopping_platform.model.User;
import com.shop.shopping_platform.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    public Order placeOrder(User user, List<Cart> cartItems) {
        Double total = cartService.getCartTotal(cartItems);

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus("placed");
        orderRepository.save(order);

        // Clear cart after order
        cartService.clearCart(user);

        return order;
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
}