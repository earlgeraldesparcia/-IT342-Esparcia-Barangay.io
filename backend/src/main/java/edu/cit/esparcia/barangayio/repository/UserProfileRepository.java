package edu.cit.esparcia.barangayio.repository;

import edu.cit.esparcia.barangayio.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    
    Optional<UserProfile> findByUserId(UUID userId);
    
    Optional<UserProfile> findByUserIdAndIsActiveTrue(UUID userId);
    
    boolean existsByUserId(UUID userId);
}
