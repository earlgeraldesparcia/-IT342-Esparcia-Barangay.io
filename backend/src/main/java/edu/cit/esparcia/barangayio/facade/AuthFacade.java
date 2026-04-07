package edu.cit.esparcia.barangayio.facade;

import edu.cit.esparcia.barangayio.model.User;
import edu.cit.esparcia.barangayio.payload.LoginRequest;
import edu.cit.esparcia.barangayio.payload.RegisterRequest;
import edu.cit.esparcia.barangayio.service.AuthService;
import edu.cit.esparcia.barangayio.service.GoogleOAuthService;
import edu.cit.esparcia.barangayio.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthFacade {
    private final AuthService authService;
    private final GoogleOAuthService googleOAuthService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthFacade(AuthService authService, GoogleOAuthService googleOAuthService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.googleOAuthService = googleOAuthService;
        this.jwtUtil = jwtUtil;
    }

    public User registerResident(RegisterRequest request) {
        return authService.registerResident(request);
    }

    public Map<String, Object> processLogin(LoginRequest request) {
        User user = authService.login(request);
        String jwt = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("role", user.getRole());
        response.put("message", "Login successful");

        return response;
    }

    public Map<String, Object> processGoogleLogin(String idToken) {
        return googleOAuthService.processGoogleLogin(idToken);
    }
}
