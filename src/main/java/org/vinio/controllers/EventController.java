package org.vinio.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vinio.dto.EventDTO;
import org.vinio.entities.Event;
import org.vinio.services.EventService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private ModelMapper modelMapper;

    // Получение списка всех событий
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.findAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Получение события по ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.findEventById(id);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Получение события по ID
    @GetMapping("/dto/{id}")
    public ResponseEntity<EventDTO> getEventDtoById(@PathVariable Long id) {
        Optional<Event> event = eventService.findEventById(id);
        EventDTO eventDTO = modelMapper.map(event, EventDTO.class);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    // Получение события по ID пользователя
    @GetMapping("/user/deprecate/{id}")
    public ResponseEntity<List<Event>> getEventByUserId(@PathVariable Long id) {
        System.out.println("!get request!");
        List<Event> events = eventService.findEventsByUserId(id);
        if (events.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Map<String, Event>> getEventsByUserId(@PathVariable Long id) {
        System.out.println("!get request!");
        List<Event> events = eventService.findEventsByUserId(id);

        if (events.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Преобразуем List<Event> в Map<String, Event>, где ключом будет, например, id события
        Map<String, Event> eventMap = new HashMap<>();
        for (Event event : events) {
            eventMap.put("event" + event.getId(), event);  // Используем ID события как ключ
        }

        return ResponseEntity.ok(eventMap);  // Возвращаем объект, содержащий события
    }



    // Добавление нового события
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        Event createdEvent = eventService.saveEvent(event);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    // Обновление данных события
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody Event event) {
        Optional<Event> existingEvent = eventService.findEventById(id);
        if (existingEvent.isPresent()) {
            event.setId(id); // Устанавливаем ID для обновления
            Event updatedEvent = eventService.saveEvent(event);
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Удаление события по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (eventService.findEventById(id).isPresent()) {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

