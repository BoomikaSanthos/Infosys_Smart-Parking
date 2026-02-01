package com.smartpark.controller;

import com.smartpark.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/custom")
    public ResponseEntity<?> getCustomAnalytics(@RequestParam Map<String, String> params) {
        try {
            return ResponseEntity.ok(analyticsService.getAnalytics(params));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/states")
    public ResponseEntity<?> getStates() {
        return ResponseEntity.ok(analyticsService.getUniqueStates());
    }

    @GetMapping("/locations")
    public ResponseEntity<?> getLocations(@RequestParam(required = false) String state) {
        return ResponseEntity.ok(analyticsService.getUniqueLocations(state));
    }

    @GetMapping("/numbers")
    public ResponseEntity<?> getNumbers(@RequestParam(required = false) String state,
                                        @RequestParam(required = false) String location) {
        return ResponseEntity.ok(analyticsService.getSlotNumbers(state, location));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(Map.of("users", analyticsService.getUsersForDropdown()));
    }
}
