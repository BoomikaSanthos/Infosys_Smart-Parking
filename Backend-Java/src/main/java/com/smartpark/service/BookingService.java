package com.smartpark.service;

import com.smartpark.model.Booking;
import com.smartpark.model.Slot;
import com.smartpark.model.User;
import com.smartpark.repository.BookingRepository;
import com.smartpark.repository.SlotRepository;
import com.smartpark.repository.UserRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, SlotRepository slotRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
    }

    // ----------------------
    // Preview and Book
    // ----------------------
    @Transactional
    public Map<String, Object> previewAndBook(
            @NonNull String userEmail,
            @NonNull String slotId,
            @NonNull String vehicleNumber,
            @NonNull Date startTime,
            @NonNull Date endTime
    ) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        if (!slot.getIsAvailable()) {
            throw new RuntimeException("Slot unavailable");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Calculate Cost
        long durationMs = endTime.getTime() - startTime.getTime();
        int slabs = (int) Math.ceil(durationMs / (15.0 * 60 * 1000));
        double cost = slabs * 5.0;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSlot(slot);
        booking.setVehicleNumber(vehicleNumber.toUpperCase());
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPlannedSlabs(slabs);
        booking.setPlannedCost(cost);

        // Initialize fields for frontend sync
        booking.setAmount(0.0);
        booking.setParkingCharge(0.0);
        booking.setPenaltyAmount(0.0);
        booking.setPenaltyType(null);
        booking.setPaymentStatus("pending");
        booking.setPenaltyPaid(false);
        booking.setPaymentMethod(null);
        booking.setStatus("active");

        bookingRepository.save(booking);

        slot.setIsAvailable(false);
        slotRepository.save(slot);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("booking", booking);
        response.put("paymentDetails", Map.of(
                "slabs", slabs,
                "totalCost", cost,
                "message", String.format("Booked! ₹%.2f (%d × 15min slabs)", cost, slabs)
        ));
        return response;
    }

    // ----------------------
    // Check-in
    // ----------------------
    public void checkIn(@NonNull String userEmail, @NonNull String bookingId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        if (!"active".equals(booking.getStatus())) {
            throw new RuntimeException("Booking inactive");
        }

        booking.setActualEntryTime(new Date());
        booking.setPaymentStatus("pending");
        bookingRepository.save(booking);
    }

    // ----------------------
    // Check-out
    // ----------------------
    public void checkOut(@NonNull String userEmail, @NonNull String bookingId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        if (booking.getActualEntryTime() == null) {
            throw new RuntimeException("Checkin first");
        }

        booking.setActualExitTime(new Date());

        long durationMs = booking.getActualExitTime().getTime() - booking.getActualEntryTime().getTime();
        int minutes = (int) (durationMs / 60000);
        int slabs = (int) Math.ceil(minutes / 15.0);

        booking.setActualDurationMinutes(minutes);
        booking.setSlabs(slabs);
        booking.setAmount(slabs * 5.0);
        booking.setParkingCharge(booking.getAmount());
        booking.setPaymentStatus("pending"); // Ready for payment

        bookingRepository.save(booking);
    }

    // ----------------------
    // Get active booking
    // ----------------------
    public Booking getActiveBooking(@NonNull String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUser(user).stream()
                .filter(b -> "active".equals(b.getStatus()))
                .findFirst()
                .orElse(null);
    }

    // ----------------------
    // Booking history
    // ----------------------
    public List<Map<String, Object>> getHistory(@NonNull String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Booking> bookings = bookingRepository.findByUser(user);

        bookings.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return bookings.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("_id", b.getId());
            map.put("startTime", b.getStartTime());
            map.put("endTime", b.getEndTime());
            map.put("vehicleNumber", b.getVehicleNumber());
            map.put("slot", b.getSlot());
            map.put("status", b.getStatus());
            map.put("amount", b.getAmount());
            map.put("parkingCharge", b.getParkingCharge());
            map.put("penaltyAmount", b.getPenaltyAmount());
            map.put("penaltyType", b.getPenaltyType());
            map.put("paymentStatus", b.getPaymentStatus());
            map.put("paymentMethod", b.getPaymentMethod());
            map.put("slabs", b.getSlabs() != null && b.getSlabs() > 0 ? b.getSlabs() : b.getPlannedSlabs());
            map.put("actualDurationMinutes", b.getActualDurationMinutes());
            return map;
        }).collect(Collectors.toList());
    }
}

