package com.smartpark.controller;

import com.smartpark.model.Company;
import com.smartpark.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCompany(@RequestBody Company company) {
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Company created successfully",
                "data", companyService.createCompany(company)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCompanies() {
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", companyService.getAllCompanies()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}

