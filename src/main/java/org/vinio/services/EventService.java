package org.vinio.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vinio.entities.Event;
import org.vinio.repositories.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Optional<Event> findEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> findEventsByUserId(Long userId) {
//        System.out.println(eventRepository.findByUserId(userId));
        return eventRepository.findByUserId(userId);
    }

    public List<Event> findEventsByUserEmail(String userEmail) {
        System.out.println("i'm here");
        System.out.println(eventRepository.findAllByUserEmail(userEmail));
        return eventRepository.findAllByUserEmail(userEmail);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }
}
