package edu.cit.esparcia.barangayio.repository;

import edu.cit.esparcia.barangayio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by Barangay ID
     */
    Optional<User> findByBarangayId(String barangayId);
    
    /**
     * Check if email already exists - prevents duplicate accounts
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if Barangay ID already exists - prevents duplicate accounts
     */
    boolean existsByBarangayId(String barangayId);
}
