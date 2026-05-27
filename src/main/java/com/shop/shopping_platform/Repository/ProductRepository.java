package com.shop.shopping_platform.Repository;

import com.shop.shopping_platform.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}