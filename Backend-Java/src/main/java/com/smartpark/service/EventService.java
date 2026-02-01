package com.smartpark.service;

import com.smartpark.model.Event;
import com.smartpark.repository.EventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("null") // <-- suppress all null-safety warnings in this class
public class EventService {

    private final EventRepository eventRepository;
    private final MongoTemplate mongoTemplate;

    public EventService(EventRepository eventRepository, MongoTemplate mongoTemplate) {
        this.eventRepository = eventRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Map<String, Object> getEvents(int page, int limit, String search, String sort) {
        Query query = new Query();
        if (search != null && !search.isEmpty()) {
            query.addCriteria(Criteria.where("eventName").regex(search, "i"));
        }

        long total = mongoTemplate.count(query, Event.class);

        // Sorting
        if ("asc".equalsIgnoreCase(sort)) {
            query.with(Sort.by(Sort.Direction.ASC, "createdAt"));
        } else {
            query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        // Pagination
        query.with(PageRequest.of(page - 1, limit));

        List<Event> events = mongoTemplate.find(query, Event.class);

        return Map.of(
            "success", true,
            "total", total,
            "page", page,
            "pages", (int) Math.ceil((double) total / limit),
            "events", events
        );
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(String id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        if (eventDetails.getEventName() != null) event.setEventName(eventDetails.getEventName());
        if (eventDetails.getDescription() != null) event.setDescription(eventDetails.getDescription());
        if (eventDetails.getDate() != null) event.setDate(eventDetails.getDate());
        return eventRepository.save(event);
    }

    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found");
        }
        eventRepository.deleteById(id);
    }

    public Event getEvent(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }
}
