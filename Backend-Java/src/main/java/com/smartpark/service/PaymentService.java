package com.smartpark.service;

import com.smartpark.model.Booking;
import com.smartpark.model.Payment;
import com.smartpark.model.Slot;
import com.smartpark.model.User;
import com.smartpark.repository.BookingRepository;
import com.smartpark.repository.PaymentRepository;
import com.smartpark.repository.SlotRepository;
import com.smartpark.repository.UserRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@SuppressWarnings("null") // <-- suppress all null-safety warnings in this class
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository,
                          SlotRepository slotRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Map<String, Object> processPayment(
            @NonNull String userEmail,
            @NonNull String bookingId,
            @NonNull Map<String, Object> payload
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if ("paid".equals(booking.getPaymentStatus())) {
            throw new RuntimeException("Already paid");
        }

        Date now = new Date();
        Date endTime = booking.getEndTime();
        Date entryTime = booking.getActualEntryTime();
        Date exitTime = booking.getActualExitTime();

        double slotUsageMinutes = 0;
        if (entryTime != null && exitTime != null) {
            slotUsageMinutes = (exitTime.getTime() - entryTime.getTime()) / 60000.0;
        } else if (entryTime != null) {
            long endRef = Math.min(now.getTime(), endTime.getTime());
            slotUsageMinutes = (endRef - entryTime.getTime()) / 60000.0;
        }

        boolean isNoShow = (entryTime == null && now.after(endTime));

        if (isNoShow || slotUsageMinutes <= 0) {
            booking.setAmount(5.0);
            booking.setParkingCharge(0.0);
            booking.setPenaltyAmount(5.0);
            booking.setPenaltyType("no-show");
        } else if (exitTime != null && (now.getTime() - exitTime.getTime()) > (24 * 60 * 60 * 1000)) {
            int slabs = (int) Math.ceil(slotUsageMinutes / 15.0);
            booking.setParkingCharge(slabs * 5.0);
            booking.setPenaltyAmount(5.0);
            booking.setPenaltyType("late-payment");
            booking.setAmount(booking.getParkingCharge() + booking.getPenaltyAmount());
        } else {
            int slabs = (int) Math.ceil(slotUsageMinutes / 15.0);
            booking.setParkingCharge(slabs * 5.0);
            booking.setPenaltyAmount(0.0);
            booking.setPenaltyType("");
            booking.setAmount(booking.getParkingCharge());
        }

        // Safe overrides from payload
        if (payload.containsKey("amount")) {
            Object amtObj = payload.get("amount");
            double amountValue = amtObj != null ? Double.parseDouble(amtObj.toString()) : booking.getAmount();
            if (amountValue > 0) booking.setAmount(amountValue);
        }

        if (payload.containsKey("parkingCharge")) {
            Object pcObj = payload.get("parkingCharge");
            double pcValue = pcObj != null ? Double.parseDouble(pcObj.toString()) : booking.getParkingCharge();
            if (pcValue >= 0) booking.setParkingCharge(pcValue);
        }

        // Safe conversion with default
        booking.setPaymentMethod(payload.getOrDefault("paymentMethod", "unknown").toString());

        booking.setPaymentStatus("paid");
        booking.setPenaltyPaid(true);
        booking.setUpdatedAt(new Date());

        Payment payment = new Payment();
        payment.setBookingId(booking);
        payment.setAmount(booking.getAmount());
        payment.setParkingCharge(booking.getParkingCharge());
        payment.setPenaltyAmount(booking.getPenaltyAmount());
        payment.setPenaltyType(booking.getPenaltyType());
        payment.setDurationMinutes((int) slotUsageMinutes);
        payment.setSlabsUsed(booking.getSlabs() != null && booking.getSlabs() > 0
                ? booking.getSlabs() : (int) Math.ceil(slotUsageMinutes / 15.0));
        payment.setStatus("paid");
        payment.setMethod(booking.getPaymentMethod());
        payment.setVehicleNumber(booking.getVehicleNumber());
        if (booking.getSlot() != null) {
            payment.setSlotNumber(booking.getSlot().getSlotNumber());
        }
        payment.setUser(user);

        paymentRepository.save(payment);
        booking.setPaymentId(payment);

        if (booking.getSlot() != null) {
            Slot slot = slotRepository.findById(booking.getSlot().getId()).orElse(null);
            if (slot != null) {
                slot.setIsAvailable(true);
                slotRepository.save(slot);
            }
        }

        booking.setStatus("completed");
        bookingRepository.save(booking);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", String.format("✅ Payment successful! ₹%.2f saved", booking.getAmount()));
        response.put("bookingId", booking.getId());
        response.put("paymentId", payment.getId());
        response.put("finalAmount", booking.getAmount());

        return response;
    }

    public Map<String, Object> getPreview(@NonNull String userEmail, @NonNull String bookingId) {
        userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Date now = new Date();
        Date endTime = booking.getEndTime();
        Date entryTime = booking.getActualEntryTime();
        Date exitTime = booking.getActualExitTime();

        double slotUsageMinutes = 0;
        if (entryTime != null && exitTime != null) {
            slotUsageMinutes = (exitTime.getTime() - entryTime.getTime()) / 60000.0;
        } else if (entryTime != null) {
            long endRef = Math.min(now.getTime(), endTime.getTime());
            slotUsageMinutes = (endRef - entryTime.getTime()) / 60000.0;
        }

        boolean isNoShow = (entryTime == null && now.after(endTime));
        double parkingCharge = 0;
        double penalty = 0;
        String penaltyType = "";

        if (isNoShow || slotUsageMinutes <= 0) {
            penalty = 5;
            penaltyType = "no-show";
        } else if (exitTime != null && (now.getTime() - exitTime.getTime()) > (24 * 60 * 60 * 1000)) {
            int slabs = (int) Math.ceil(slotUsageMinutes / 15.0);
            parkingCharge = slabs * 5;
            penalty = 5;
            penaltyType = "late-payment";
        } else {
            int slabs = (int) Math.ceil(slotUsageMinutes / 15.0);
            parkingCharge = slabs * 5;
        }

        return Map.of(
                "slotUsageMinutes", Math.round(slotUsageMinutes),
                "slabs", Math.ceil(slotUsageMinutes / 15.0),
                "parkingCharge", parkingCharge,
                "penalty", penalty,
                "penaltyType", penaltyType,
                "totalDue", parkingCharge + penalty,
                "isNoShow", isNoShow
        );
    }
}
