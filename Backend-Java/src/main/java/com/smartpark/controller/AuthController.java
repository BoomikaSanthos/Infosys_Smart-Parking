package com.smartpark.controller;

import com.smartpark.model.User;
import com.smartpark.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> payload) {
        try {
            // Mapping manually because payload contains more than just User
            User user = new User();
            user.setName((String) payload.get("name"));
            user.setEmail((String) payload.get("email"));
            user.setPassword((String) payload.get("password"));
            user.setVehicleNumber((String) payload.get("vehicleNumber"));
            user.setVehicleType((String) payload.get("vehicleType"));
            user.setPhone((String) payload.get("phone"));
            user.setRole((String) payload.get("role"));

            String otp = (String) payload.get("otp");
            Boolean resendOtp = (Boolean) payload.get("resendOtp");

            return ResponseEntity.ok(authService.register(user, otp, resendOtp));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            return ResponseEntity.ok(authService.login(
                request.get("email"),
                request.get("password"),
                request.get("role"),
                request.get("otp")
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody Map<String, String> request) {
        try {
            authService.login(
                request.get("email"),
                request.get("password"),
                request.get("role"),
                null // Try without OTP first for validation
            );
            return ResponseEntity.ok(Map.of("message", "Valid credentials"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }
}

