package edu.cit.esparcia.barangayio.controller;

import edu.cit.esparcia.barangayio.model.User;
import edu.cit.esparcia.barangayio.payload.LoginRequest;
import edu.cit.esparcia.barangayio.payload.MessageResponse;
import edu.cit.esparcia.barangayio.payload.RegisterRequest;
import edu.cit.esparcia.barangayio.service.AuthService;
import edu.cit.esparcia.barangayio.service.GoogleOAuthService;
import edu.cit.esparcia.barangayio.util.JwtUtil;
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
    private final GoogleOAuthService googleOAuthService;
    private final JwtUtil jwtUtil;
    
    @Autowired
    public AuthController(AuthService authService, GoogleOAuthService googleOAuthService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.googleOAuthService = googleOAuthService;
        this.jwtUtil = jwtUtil;
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
     * Validates email and password, returns JWT token on success
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            User user = authService.login(request);
            
            // Generate JWT token
            String jwt = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
            
            // Create response with user info and token
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("userId", user.getId());
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
    
    /**
     * Google OAuth Login Endpoint
     * 
     * Receives Google ID token, verifies it, creates/updates user, returns JWT
     */
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        try {
            String idToken = request.get("idToken");
            if (idToken == null || idToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("ID token is required"));
            }
            
            Map<String, Object> result = googleOAuthService.processGoogleLogin(idToken);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse(e.getMessage()));
        }
    }
}
