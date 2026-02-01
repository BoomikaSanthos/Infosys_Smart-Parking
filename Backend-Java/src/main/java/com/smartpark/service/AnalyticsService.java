package com.smartpark.service;

import com.smartpark.model.Slot;
import com.smartpark.model.User;
import com.smartpark.repository.SlotRepository;
import com.smartpark.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;
    private final SlotRepository slotRepository; // optional
    private final UserRepository userRepository;

    // ----------------------
    // Analytics
    // ----------------------
    public Map<String, Object> getAnalytics(@NonNull Map<String, String> params) {
        String time = params.get("time");
        String state = params.get("state");
        String location = params.get("location");
        String slotNumber = params.get("slotNumber");

        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();

        // Time filter
        if ("day".equals(time)) {
            query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("createdAt")
                    .gte(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)));
        } else if ("week".equals(time)) {
            query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("createdAt")
                    .gte(new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000)));
        }

        // Slot filter
        if (slotNumber != null && !slotNumber.isEmpty()) {
            slotRepository.findBySlotNumber(slotNumber)
                    .ifPresent(value -> query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("slot").is(value.getId())));
        } else if (location != null && !location.isEmpty()) {
            List<Slot> slots = Optional.ofNullable(
                    mongoTemplate.find(
                            org.springframework.data.mongodb.core.query.Query.query(
                                    org.springframework.data.mongodb.core.query.Criteria.where("location").is(location.toUpperCase())
                            ),
                            Slot.class
                    )
            ).orElse(Collections.emptyList());

            if (!slots.isEmpty()) {
                query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("slot")
                        .in(slots.stream().map(Slot::getId).collect(Collectors.toList())));
            }
        } else if (state != null && !state.isEmpty()) {
            List<Slot> slots = Optional.ofNullable(
                    mongoTemplate.find(
                            org.springframework.data.mongodb.core.query.Query.query(
                                    org.springframework.data.mongodb.core.query.Criteria.where("state").is(state.toUpperCase())
                            ),
                            Slot.class
                    )
            ).orElse(Collections.emptyList());

            if (!slots.isEmpty()) {
                query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("slot")
                        .in(slots.stream().map(Slot::getId).collect(Collectors.toList())));
            }
        }

        List<com.smartpark.model.Booking> bookings = Optional.ofNullable(
                mongoTemplate.find(query, com.smartpark.model.Booking.class)
        ).orElse(Collections.emptyList());

        long totalBookings = bookings.size();
        double totalRevenue = bookings.stream()
                .filter(b -> !"no-show".equals(b.getPaymentStatus()))
                .mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0)
                .sum();
        long activeBookings = bookings.stream().filter(b -> "active".equals(b.getStatus())).count();
        long pendingPayments = bookings.stream().filter(b -> "pending".equals(b.getPaymentStatus())).count();
        double avgDuration = bookings.stream()
                .mapToInt(b -> b.getActualDurationMinutes() != null ? b.getActualDurationMinutes() : 0)
                .average().orElse(0.0);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalBookings", totalBookings);
        metrics.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);
        metrics.put("activeBookings", activeBookings);
        metrics.put("pendingPayments", pendingPayments);
        metrics.put("averageDurationHours", Math.round((avgDuration / 60.0) * 10.0) / 10.0);
        metrics.put("peakHours", new ArrayList<>()); // Placeholder
        metrics.put("slotUsage", new ArrayList<>());  // Placeholder
        metrics.put("topUsers", new ArrayList<>());   // Placeholder

        return metrics;
    }

    public List<String> getUniqueStates() {
        return Optional.ofNullable(
                mongoTemplate.findDistinct(
                        new org.springframework.data.mongodb.core.query.Query(),
                        "state",
                        Slot.class,
                        String.class
                )
        ).orElse(Collections.emptyList());
    }

    public List<String> getUniqueLocations(String state) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        if (state != null && !state.isEmpty()) {
            query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("state").is(state.toUpperCase()));
        }
        return Optional.ofNullable(
                mongoTemplate.findDistinct(query, "location", Slot.class, String.class)
        ).orElse(Collections.emptyList());
    }

    public List<String> getSlotNumbers(String state, String location) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        if (state != null && !state.isEmpty()) {
            query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("state").is(state.toUpperCase()));
        }
        if (location != null && !location.isEmpty()) {
            query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("location").is(location.toUpperCase()));
        }
        return Optional.ofNullable(
                mongoTemplate.findDistinct(query, "slotNumber", Slot.class, String.class)
        ).orElse(Collections.emptyList());
    }

    public List<User> getUsersForDropdown() {
        return Optional.ofNullable(userRepository.findAll()).orElse(Collections.emptyList());
    }
}
