package com.smartpark.service;

import com.smartpark.model.Booking;
import com.smartpark.model.Payment;
import com.smartpark.model.User;
import com.smartpark.repository.BookingRepository;
import com.smartpark.repository.PaymentRepository;
import com.smartpark.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public UserService(UserRepository userRepository, BookingRepository bookingRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    // ---------------- USER PROFILE ----------------

    public User getUserProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ---------------- USER DASHBOARD ----------------

    public Map<String, Object> getProfileDashboard(String email) {

        User user = getUserProfile(email);

        List<Booking> bookings = bookingRepository.findByUser(user);
        List<Payment> payments = paymentRepository.findByUser(user);

        // ---- BASIC STATS ----

        long totalBookings = bookings.size();

        double totalSpent = bookings.stream()
                .mapToDouble(b -> b.getAmount() != null ? b.getAmount() : 0.0)
                .sum();

        double avgDuration = bookings.stream()
                .mapToInt(b -> b.getActualDurationMinutes() != null ? b.getActualDurationMinutes() : 0)
                .average()
                .orElse(0);

        // ---- MONTHLY SPENDS ----

        Map<String, Double> monthlySpendsMap = new HashMap<>();

        for (Booking b : bookings) {
            if (b.getCreatedAt() != null) {

                String month = b.getCreatedAt()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toString()
                        .substring(0, 7); // YYYY-MM

                double amount = b.getAmount() != null ? b.getAmount() : 0.0;

                monthlySpendsMap.merge(
                        month,
                        amount,
                        (oldVal, newVal) -> oldVal + newVal
                );
            }
        }

        List<Map<String, Object>> monthlySpends = monthlySpendsMap.entrySet()
                .stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("month", e.getKey());
                    m.put("spend", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());

        // ---- FINAL RESPONSE ----

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", totalBookings);
        stats.put("totalSpent", totalSpent);
        stats.put("avgDuration", avgDuration);
        stats.put("monthlySpends", monthlySpends);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("bookings", bookings);
        response.put("payments", payments);
        response.put("stats", stats);

        return response;
    }
}
