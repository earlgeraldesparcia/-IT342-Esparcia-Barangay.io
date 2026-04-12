package edu.cit.esparcia.barangayio.repository;

import edu.cit.esparcia.barangayio.model.CertificateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateTypeRepository extends JpaRepository<CertificateType, Long> {
    
    Optional<CertificateType> findByName(String name);
    
    boolean existsByName(String name);
}
