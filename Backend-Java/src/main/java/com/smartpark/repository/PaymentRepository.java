package com.smartpark.repository;

import com.smartpark.model.Booking;
import com.smartpark.model.Payment;
import com.smartpark.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByBookingId(Booking booking);
    List<Payment> findByUser(User user);
}
