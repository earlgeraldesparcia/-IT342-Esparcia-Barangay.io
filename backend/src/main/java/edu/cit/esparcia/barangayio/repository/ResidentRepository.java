package edu.cit.esparcia.barangayio.repository;

import edu.cit.esparcia.barangayio.model.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, UUID> {
    
    List<Resident> findByUserProfileId(UUID userProfileId);
    
    Optional<Resident> findByUserProfileIdAndEmail(UUID userProfileId, String email);
    
    boolean existsByUserProfileId(UUID userProfileId);
}
