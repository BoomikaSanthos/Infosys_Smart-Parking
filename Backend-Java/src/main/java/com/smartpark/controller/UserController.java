package com.smartpark.controller;

import com.smartpark.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getUserProfile(principal.getName()));
    }

    @GetMapping("/profile-dashboard")
    public ResponseEntity<?> getProfileDashboard(Principal principal) {
        return ResponseEntity.ok(userService.getProfileDashboard(principal.getName()));
    }

    @GetMapping("/all") // Open or Admin? Node: Open maybe? But good to secure.
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers()); // Caution: exposing all users
    }
}

