package edu.cit.esparcia.barangayio.service;

import edu.cit.esparcia.barangayio.model.User;
import edu.cit.esparcia.barangayio.repository.UserRepository;
import edu.cit.esparcia.barangayio.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class GoogleOAuthService {
    
    @Value("${google.client.id:}")
    private String googleClientId;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Verify Google ID token and get user info
     */
    public Map<String, Object> verifyGoogleToken(String idToken) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> userInfo = response.getBody();
            
            // Verify the token is intended for our client
            String aud = (String) userInfo.get("aud");
            if (!googleClientId.isEmpty() && !googleClientId.equals(aud)) {
                throw new RuntimeException("Invalid token audience");
            }
            
            return userInfo;
        }
        
        throw new RuntimeException("Failed to verify Google token");
    }
    
    /**
     * Process Google login - create user if not exists, generate JWT
     */
    public Map<String, Object> processGoogleLogin(String idToken) {
        Map<String, Object> googleUserInfo = verifyGoogleToken(idToken);
        
        String email = (String) googleUserInfo.get("email");
        String firstName = (String) googleUserInfo.get("given_name");
        String lastName = (String) googleUserInfo.get("family_name");
        
        // Check if user exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;
        
        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Update auth provider if not set
            if (!"google".equals(user.getAuthProvider())) {
                user.setAuthProvider("google");
                user = userRepository.save(user);
            }
        } else {
            // Create new user
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName != null ? firstName : "");
            user.setLastName(lastName != null ? lastName : "");
            user.setPasswordHash("GOOGLE_OAUTH"); // Placeholder, not used for OAuth users
            user.setPhoneNumber(""); // Required field
            user.setAddress(""); // Required field
            user.setRole("resident");
            user.setAuthProvider("google");
            user = userRepository.save(user);
        }
        
        // Generate JWT
        String jwt = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole(), user.getFirstName());
        
        return Map.of(
            "token", jwt,
            "userId", user.getId(),
            "email", user.getEmail(),
            "firstName", user.getFirstName(),
            "lastName", user.getLastName(),
            "role", user.getRole(),
            "authProvider", user.getAuthProvider()
        );
    }
}
