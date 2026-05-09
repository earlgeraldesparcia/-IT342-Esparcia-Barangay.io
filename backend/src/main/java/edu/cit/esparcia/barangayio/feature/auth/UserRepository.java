package edu.cit.esparcia.barangayio.feature.auth;

import edu.cit.esparcia.barangayio.feature.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if email already exists - prevents duplicate accounts
     */
    boolean existsByEmail(String email);
}
