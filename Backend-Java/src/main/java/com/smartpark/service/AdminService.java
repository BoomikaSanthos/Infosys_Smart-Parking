package com.smartpark.service;

import com.smartpark.model.Booking;
import com.smartpark.model.Slot;
import com.smartpark.model.User;
import com.smartpark.model.Payment;
import com.smartpark.repository.BookingRepository;
import com.smartpark.repository.PaymentRepository;
import com.smartpark.repository.SlotRepository;
import com.smartpark.repository.UserRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public AdminService(UserRepository userRepository, SlotRepository slotRepository,
                        BookingRepository bookingRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    // ----------------------
    // Admin validation
    // ----------------------
    private void checkAdmin(@NonNull String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!"admin".equals(user.getRole())) {
            throw new RuntimeException("Access denied. Admin only.");
        }
    }

    // ----------------------
    // Dashboard statistics
    // ----------------------
    public Map<String, Object> getDashboard(@NonNull String adminEmail) {
        checkAdmin(adminEmail);

        List<User> allUsers = userRepository.findAll();
        List<Slot> allSlots = slotRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAll();
        List<Payment> allPayments = paymentRepository.findAll();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", allUsers.size());
        stats.put("totalSlots", allSlots.size());
        stats.put("totalBookings", allBookings.size());
        stats.put("totalPayments", allPayments.size());
        stats.put("occupiedSlots", allSlots.stream().filter(s -> !s.getIsAvailable()).count());
        stats.put("totalRevenue", allPayments.stream()
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0)
                .sum());

        return Map.of(
                "rawData", Map.of(
                        "allUsers", allUsers,
                        "allSlots", allSlots,
                        "allBookings", allBookings,
                        "allPayments", allPayments
                ),
                "stats", stats
        );
    }

    // ----------------------
    // Get all raw data
    // ----------------------
    public Map<String, Object> getAllData(@NonNull String adminEmail) {
        checkAdmin(adminEmail);
        return Map.of(
                "users", userRepository.findAll(),
                "slots", slotRepository.findAll(),
                "bookings", bookingRepository.findAll(),
                "payments", paymentRepository.findAll()
        );
    }

    // ----------------------
    // Users
    // ----------------------
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ----------------------
    // Slots
    // ----------------------
    public Slot createSlot(@NonNull Slot slot) {
        return slotRepository.save(slot);
    }

    public List<Slot> getAllSlots() {
        return slotRepository.findAll();
    }

    public void deleteSlot(@NonNull String slotId) {
        slotRepository.deleteById(slotId);
    }

    // ----------------------
    // Bookings
    // ----------------------
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
