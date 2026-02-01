package com.smartpark.controller;

import com.smartpark.model.Slot;
import com.smartpark.service.SlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSlot(@RequestBody Slot slot) {
        try {
            return ResponseEntity.status(201).body(Map.of("message", "Slot added", "slot", slotService.addSlot(slot)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error adding slot", "error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{slotNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeSlot(@PathVariable String slotNumber) {
        try {
            return ResponseEntity.ok(Map.of("message", "Slot removed", "slot", slotService.removeSlot(slotNumber)));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/manage/{slotNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSlot(@PathVariable String slotNumber, @RequestBody Slot slot) {
        try {
            return ResponseEntity.ok(Map.of("message", "Slot updated", "slot", slotService.updateSlot(slotNumber, slot)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error updating slot", "error", e.getMessage()));
        }
    }

    @GetMapping("/with-status")
    public ResponseEntity<?> getSlotsWithStatus(@RequestParam(required = false) String state,
                                                @RequestParam(required = false) String location) {
        return ResponseEntity.ok(slotService.getSlotsWithStatus(state, location));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSlots() {
        return ResponseEntity.ok(Map.of("slots", slotService.getAllSlots()));
    }

    @GetMapping("/states")
    public ResponseEntity<?> getStates() {
        return ResponseEntity.ok(slotService.getDistinctStates());
    }

    @GetMapping("/locations")
    public ResponseEntity<?> getLocations(@RequestParam String state) {
        return ResponseEntity.ok(slotService.getDistinctLocations(state));
    }

    @GetMapping("/numbers")
    public ResponseEntity<?> getSlotNumbers(@RequestParam String state, @RequestParam String location) {
        return ResponseEntity.ok(slotService.getSlotNumbers(state, location));
    }
}
