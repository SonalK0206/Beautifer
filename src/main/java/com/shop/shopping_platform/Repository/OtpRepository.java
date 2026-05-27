package com.shop.shopping_platform.Repository;

import com.shop.shopping_platform.model.OtpStore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpStore, Long> {
    Optional<OtpStore> findByEmail(String email);
    void deleteByEmail(String email);
}