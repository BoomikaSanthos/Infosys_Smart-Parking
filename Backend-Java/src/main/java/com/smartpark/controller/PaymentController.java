package com.smartpark.controller;

import com.smartpark.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay/{id}")
    public ResponseEntity<?> pay(@PathVariable String id, @RequestBody Map<String, Object> payload, Principal principal) {
        try {
            return ResponseEntity.ok(paymentService.processPayment(principal.getName(), id, payload));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<?> preview(@PathVariable String id, Principal principal) {
        try {
            return ResponseEntity.ok(paymentService.getPreview(principal.getName(), id));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}

