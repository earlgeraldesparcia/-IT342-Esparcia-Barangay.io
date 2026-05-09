package edu.cit.esparcia.barangayio.feature.certificate;

import edu.cit.esparcia.barangayio.feature.certificate.CertificateType;
import edu.cit.esparcia.barangayio.feature.certificate.CertificateTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CertificateTypeBootstrapRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CertificateTypeBootstrapRunner.class);

    private final CertificateTypeRepository certificateTypeRepository;

    public CertificateTypeBootstrapRunner(CertificateTypeRepository certificateTypeRepository) {
        this.certificateTypeRepository = certificateTypeRepository;
    }

    @Override
    public void run(String... args) {
        if (certificateTypeRepository.count() > 0) {
            log.info("Certificate types already exist, skipping initialization");
            return;
        }

        log.info("Initializing certificate types...");

        CertificateType barangayClearance = new CertificateType();
        barangayClearance.setName("barangay_clearance");
        barangayClearance.setDescription("Barangay Clearance - A document certifying that a person is a resident of the barangay and has no derogatory record");
        certificateTypeRepository.save(barangayClearance);

        CertificateType certificateOfIndigency = new CertificateType();
        certificateOfIndigency.setName("certificate_of_indigency");
        certificateOfIndigency.setDescription("Certificate of Indigency - A document certifying that a person belongs to a low-income family and has no regular source of income");
        certificateTypeRepository.save(certificateOfIndigency);

        CertificateType communityTaxCertificate = new CertificateType();
        communityTaxCertificate.setName("community_tax_certificate");
        communityTaxCertificate.setDescription("Community Tax Certificate - A document certifying that a person has paid their community tax for the current year");
        certificateTypeRepository.save(communityTaxCertificate);

        CertificateType soloParentCertificate = new CertificateType();
        soloParentCertificate.setName("solo_parent_certificate");
        soloParentCertificate.setDescription("Solo Parent Certificate - A document certifying that a person is a solo parent and is entitled to benefits under the Solo Parents Welfare Act");
        certificateTypeRepository.save(soloParentCertificate);

        log.info("Certificate types initialized successfully");
    }
}
