package com.payment.app.repositories;

import com.payment.app.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByOrderId(Long orderId);
    
    Optional<Payment> findByPreferenceId(String preferenceId);
    
    Optional<Payment> findByProviderRef(String providerRef);
}
