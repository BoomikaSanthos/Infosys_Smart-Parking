package com.smartpark.repository;

import com.smartpark.model.Booking;
import com.smartpark.model.Slot;
import com.smartpark.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByUser(User user);
    List<Booking> findBySlotAndStatus(Slot slot, String status);
    List<Booking> findByStatus(String status);
}
