package com.smartpark.controller;

import com.smartpark.model.Event;
import com.smartpark.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<?> getEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {
        try {
            return ResponseEntity.ok(eventService.getEvents(page, limit, search, sort));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/events")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEvent(@RequestBody @NonNull Event event) {
        try {
            return ResponseEntity.status(201).body(Map.of(
                    "message", "✅ Event created successfully",
                    "event", eventService.createEvent(event)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEvent(@PathVariable @NonNull String id, @RequestBody @NonNull Event event) {
        try {
            return ResponseEntity.ok(Map.of(
                    "message", "✅ Event updated successfully",
                    "event", eventService.updateEvent(id, event)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEvent(@PathVariable @NonNull String id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok(Map.of("message", "✅ Event deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEvent(@PathVariable @NonNull String id) {
        try {
            return ResponseEntity.ok(Map.of("event", eventService.getEvent(id)));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}

