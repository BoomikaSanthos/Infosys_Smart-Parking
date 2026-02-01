package com.smartpark.repository;

import com.smartpark.model.Slot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlotRepository extends MongoRepository<Slot, String> {
    Optional<Slot> findBySlotNumber(String slotNumber);
}
