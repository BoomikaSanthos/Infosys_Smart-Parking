package com.smartpark.service;

import com.smartpark.model.Slot;
import com.smartpark.repository.SlotRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SlotService {

    private final SlotRepository slotRepository;
    private final MongoTemplate mongoTemplate;

    public SlotService(SlotRepository slotRepository, MongoTemplate mongoTemplate) {
        this.slotRepository = slotRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // ---------------- SLOT CRUD ----------------

    public Slot addSlot(Slot slot) {
        if (slot.getState() != null) slot.setState(slot.getState().toUpperCase());
        if (slot.getLocation() != null) slot.setLocation(slot.getLocation().toUpperCase());

        updateAvailability(slot);
        return slotRepository.save(Objects.requireNonNull(slot));
    }

    public Slot removeSlot(String slotNumber) {
        Slot slot = slotRepository.findBySlotNumber(slotNumber)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        slotRepository.delete(Objects.requireNonNull(slot));
        return slot;
    }

    public Slot updateSlot(String slotNumber, Slot params) {
        Slot slot = slotRepository.findBySlotNumber(slotNumber)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (params.getState() != null) slot.setState(params.getState().toUpperCase());
        if (params.getLocation() != null) slot.setLocation(params.getLocation().toUpperCase());
        if (params.getSlotStatus() != null) slot.setSlotStatus(params.getSlotStatus());
        if (params.getVehicleType() != null) slot.setVehicleType(params.getVehicleType());
        if (params.getAlerts() != null) slot.setAlerts(params.getAlerts());

        updateAvailability(slot);
        return slotRepository.save(Objects.requireNonNull(slot));
    }

    private void updateAvailability(Slot slot) {
        Slot.Alerts alerts = slot.getAlerts();
        if (alerts == null) {
            alerts = new Slot.Alerts();
            slot.setAlerts(alerts);
        }
        slot.setIsAvailable(!(alerts.isSystemError() || alerts.isMaintenance() || alerts.isInfrastructure()));
    }

    // ---------------- SLOT QUERIES ----------------

    public Map<String, Object> getSlotsWithStatus(String state, String location) {
        Query query = new Query();
        if (state != null) query.addCriteria(Criteria.where("state").is(state.toUpperCase()));
        if (location != null) query.addCriteria(Criteria.where("location").is(location.toUpperCase()));

        List<Slot> slots = mongoTemplate.find(query, Slot.class);
        long availableCount = slots.stream().filter(Slot::getIsAvailable).count();

        return Map.of(
                "slots", slots,
                "stats", Map.of("total", slots.size(), "available", availableCount)
        );
    }

    public List<Slot> getAllSlots() {
        return slotRepository.findAll();
    }

    public List<String> getDistinctStates() {
        return mongoTemplate.findDistinct(new Query(), "state", Slot.class, String.class);
    }

    public List<String> getDistinctLocations(String state) {
        Query query = new Query();
        if (state != null) query.addCriteria(Criteria.where("state").is(state));
        return mongoTemplate.findDistinct(query, "location", Slot.class, String.class);
    }

    public List<String> getSlotNumbers(String state, String location) {
        Query query = new Query();
        if (state != null) query.addCriteria(Criteria.where("state").is(state));
        if (location != null) query.addCriteria(Criteria.where("location").is(location));
        return mongoTemplate.findDistinct(query, "slotNumber", Slot.class, String.class);
    }
}
