package edu.cit.esparcia.barangayio.service;

import edu.cit.esparcia.barangayio.model.User;
import edu.cit.esparcia.barangayio.payload.RegisterRequest;
import edu.cit.esparcia.barangayio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Register a new resident user
     * 
     * Duplicate Account Prevention:
     * - Checks if email already exists in the database
     * - Checks if Barangay ID is already registered
     * - Both fields must be unique to prevent duplicate accounts
     * 
     * Password Security:
     * - Passwords are hashed using BCrypt before storage
     * - BCrypt automatically generates a salt for each password
     * - This ensures passwords are never stored in plain text
     */
    @Transactional
    public User registerResident(RegisterRequest request) {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email address is already registered");
        }
        
        // Check for duplicate Barangay ID
        if (userRepository.existsByBarangayId(request.getBarangayId())) {
            throw new RuntimeException("Barangay ID is already registered");
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        
        // Secure password storage using BCrypt
        // The password is hashed with a random salt before storage
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setBarangayId(request.getBarangayId());
        user.setRole(User.UserRole.RESIDENT);
        user.setStatus(User.UserStatus.PENDING); // Requires approval
        
        return userRepository.save(user);
    }
}
