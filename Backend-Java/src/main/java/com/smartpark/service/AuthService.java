package com.smartpark.service;

import com.smartpark.model.User;
import com.smartpark.repository.UserRepository;
import com.smartpark.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    private String generateOTP() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    public Map<String, Object> register(User user, String otp, Boolean resendOtp) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent() && !Boolean.TRUE.equals(resendOtp) && otp == null) {
            throw new RuntimeException("Email already exists");
        }

        if ("admin".equals(user.getRole())) {
            if (otp == null) {
                String otpCode = generateOTP();
                user.setOtp(otpCode);
                user.setOtpExpires(new Date(System.currentTimeMillis() + 10 * 60 * 1000));
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);

                emailService.sendEmail(user.getEmail(), "ParkSmart Admin OTP", "Your OTP: " + otpCode);
                return Map.of("otpRequired", true);
            }

            User admin = existingUser.orElseThrow(() -> new RuntimeException("User not found"));
            if (!otp.equals(admin.getOtp())) {
                throw new RuntimeException("Invalid OTP");
            }
            admin.setOtp(null);
            admin.setOtpExpires(null);
            userRepository.save(admin);
            return Map.of("message", "Admin registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return Map.of("message", "User registered");
    }

    public Map<String, Object> login(String email, String password, String role, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals(role)) {
            throw new RuntimeException("Access denied for this role");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if ("admin".equals(role)) {
            if (otp == null) {
                String otpCode = generateOTP();
                user.setOtp(otpCode);
                user.setOtpExpires(new Date(System.currentTimeMillis() + 10 * 60 * 1000));
                userRepository.save(user);

                emailService.sendEmail(user.getEmail(), "ParkSmart Admin OTP", "Your OTP: " + otpCode);
                return Map.of("otpRequired", true);
            }

            if (!otp.equals(user.getOtp())) {
                throw new RuntimeException("Invalid OTP");
            }
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return Map.of("token", token, "role", user.getRole());
    }
}
