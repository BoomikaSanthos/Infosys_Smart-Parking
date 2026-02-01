package com.smartpark.controller;

import com.smartpark.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Principal principal) {
        try {
            return ResponseEntity.ok(adminService.getDashboard(principal.getName()));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/alldata")
    public ResponseEntity<?> getAllData(Principal principal) {
        try {
            return ResponseEntity.ok(adminService.getAllData(principal.getName()));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}

