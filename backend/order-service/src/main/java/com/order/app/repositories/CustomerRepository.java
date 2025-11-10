package com.order.app.repositories;

import com.order.app.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmailAndDeletedAtIsNull(String email);
    
    Optional<Customer> findByUserIdAndDeletedAtIsNull(Long userId);
    
    @Query("SELECT c FROM Customer c WHERE c.email = :email AND c.deletedAt IS NULL")
    Optional<Customer> findActiveByEmail(@Param("email") String email);
    
    @Query("SELECT c FROM Customer c WHERE c.userId = :userId AND c.deletedAt IS NULL")
    Optional<Customer> findActiveByUserId(@Param("userId") Long userId);
    
    boolean existsByEmailAndDeletedAtIsNull(String email);
    
    boolean existsByUserIdAndDeletedAtIsNull(Long userId);
}
