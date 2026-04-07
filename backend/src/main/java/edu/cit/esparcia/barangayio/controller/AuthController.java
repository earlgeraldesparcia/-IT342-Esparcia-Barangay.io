package edu.cit.esparcia.barangayio.controller;

import edu.cit.esparcia.barangayio.facade.AuthFacade;
import edu.cit.esparcia.barangayio.payload.LoginRequest;
import edu.cit.esparcia.barangayio.payload.MessageResponse;
import edu.cit.esparcia.barangayio.payload.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    
    // Controller now only depends on the unified Facade
    private final AuthFacade authFacade;
    
    @Autowired
    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            authFacade.registerResident(request);
            return ResponseEntity.ok(new MessageResponse("Registration successful."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            // Orchestration logic has been moved entirely to the Facade
            Map<String, Object> response = authFacade.processLogin(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        try {
            String idToken = request.get("idToken");
            if (idToken == null || idToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("ID token is required"));
            }
            
            Map<String, Object> result = authFacade.processGoogleLogin(idToken);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse(e.getMessage()));
        }
    }
}