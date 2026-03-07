package edu.cit.esparcia.barangayio.controller;

import edu.cit.esparcia.barangayio.model.User;
import edu.cit.esparcia.barangayio.payload.LoginRequest;
import edu.cit.esparcia.barangayio.payload.MessageResponse;
import edu.cit.esparcia.barangayio.payload.RegisterRequest;
import edu.cit.esparcia.barangayio.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    
    private final AuthService authService;
    
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * User Registration Endpoint
     * 
     * Registration Fields:
     * - firstName: User's first name (required)
     * - lastName: User's last name (required)
     * - email: Valid email address (required, unique)
     * - password: Minimum 8 characters (required)
     * - phoneNumber: Contact number (required)
     * - address: Residential address (required)
     * - barangayId: Barangay identification number (required, unique)
     * 
     * Validation Process:
     * 1. @Valid annotation triggers Jakarta validation on RegisterRequest
     * 2. Checks for valid email format
     * 3. Ensures password meets minimum length requirement
     * 4. All required fields must be present
     * 
     * Duplicate Account Prevention:
     * - Email must be unique (checked in AuthService)
     * - Barangay ID must be unique (checked in AuthService)
     * - Returns 400 Bad Request if duplicates found
     * 
     * Password Security:
     * - Password is hashed using BCrypt before storage
     * - Never stored in plain text
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.registerResident(request);
            return ResponseEntity.ok(new MessageResponse(
                "Registration successful."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * User Login Endpoint
     * 
     * Validates email and password, returns user info on success
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.login(request);
            
            // Create response with user info (excluding password)
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole());
            response.put("message", "Login successful");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse(e.getMessage()));
        }
    }
}
