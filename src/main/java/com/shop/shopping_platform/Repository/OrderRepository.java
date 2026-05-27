package com.shop.shopping_platform.Repository;

import com.shop.shopping_platform.model.Order;
import com.shop.shopping_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}