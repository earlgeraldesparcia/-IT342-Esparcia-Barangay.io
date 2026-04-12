package edu.cit.esparcia.barangayio.config;

import edu.cit.esparcia.barangayio.model.User;
import edu.cit.esparcia.barangayio.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates a default admin account when enabled (local/demo).
 * Self-registration remains resident-only; admins are not created through public signup.
 */
@Component
public class BootstrapAdminRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap.admin.email:admin@barangay.io}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:Admin@123456}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.first-name:System}")
    private String adminFirstName;

    @Value("${app.bootstrap.admin.last-name:Administrator}")
    private String adminLastName;

    public BootstrapAdminRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        User admin = new User();
        admin.setEmail(adminEmail.trim().toLowerCase());
        admin.setFirstName(adminFirstName);
        admin.setLastName(adminLastName);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setPhoneNumber("0000000000");
        admin.setAddress("Barangay office");
        admin.setRole("admin");
        admin.setAuthProvider("local");
        userRepository.save(admin);
        log.warn("Bootstrap admin created: {} (change password in production; disable app.bootstrap.admin.enabled)", adminEmail);
    }
}
