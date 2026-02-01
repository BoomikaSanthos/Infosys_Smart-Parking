package com.smartpark.controller;

import com.smartpark.model.Booking;
import com.smartpark.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/preview-and-book")
    public ResponseEntity<?> previewAndBook(@RequestBody Map<String, Object> payload, Principal principal) {
        try {
            String slotId = (String) payload.get("slotId");
            String vehicleNumber = (String) payload.get("vehicleNumber");
            Date startTime = Date.from(java.time.Instant.parse((String) payload.get("startTime")));
            Date endTime = Date.from(java.time.Instant.parse((String) payload.get("endTime")));

            return ResponseEntity.ok(
                bookingService.previewAndBook(principal.getName(), slotId, vehicleNumber, startTime, endTime)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/checkin")
    public ResponseEntity<?> checkIn(@PathVariable String id, Principal principal) {
        try {
            bookingService.checkIn(principal.getName(), id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Timer started", "entryTime", new Date()));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/checkout")
    public ResponseEntity<?> checkOut(@PathVariable String id, Principal principal) {
        try {
            bookingService.checkOut(principal.getName(), id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Timer stopped", "exitTime", new Date()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my-active")
    public ResponseEntity<?> getActiveBooking(Principal principal) {
        Booking booking = bookingService.getActiveBooking(principal.getName());
        if (booking == null) {
            return ResponseEntity.ok(Map.of("message", "No active booking"));
        }
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(Principal principal) {
        var bookings = bookingService.getHistory(principal.getName());
        return ResponseEntity.ok(Map.of("bookings", bookings, "total", bookings.size()));
    }
}
