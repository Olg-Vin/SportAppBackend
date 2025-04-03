package org.vinio.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vinio.dto.EventDTO;
import org.vinio.entities.Event;
import org.vinio.entities.UserEntity;
import org.vinio.jwt.JwtProvider;
import org.vinio.services.EventService;
import org.vinio.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;

    // Получение списка всех событий
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.findAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Event>> getAllEventsByUserName(@RequestHeader("Authorization") String authorizationHeader) {
        // Извлекаем токен из заголовка Authorization (удаляем "Bearer " в начале)
        String token = authorizationHeader.replace("Bearer ", "");

        // Извлекаем имя пользователя из токена
        String username = jwtProvider.getAccessTokenUsername(token);
        System.out.println("my name is " + username);
        // Теперь у вас есть имя пользователя, и вы можете получить его события
        List<Event> events = eventService.findEventsByUserEmail(username);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }


    // Получение события по ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.findEventById(id);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Получение события по ID
    @GetMapping(value = "/dto/{id}", produces = "application/json")
    public ResponseEntity<EventDTO> getEventDtoById(@PathVariable Long id) {
        Optional<Event> event = eventService.findEventById(id);
        EventDTO eventDTO = modelMapper.map(event, EventDTO.class);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    // Получение события по ID пользователя
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Event>> getEventByUserId(@PathVariable Long id) {
        System.out.println("!get request!");
        List<Event> events = eventService.findEventsByUserId(id);
        if (events.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/user/dep/{id}")
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
    public ResponseEntity<Event> createEvent(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody Event event) {
        // Извлекаем токен из заголовка Authorization (удаляем "Bearer " в начале)
        String token = authorizationHeader.replace("Bearer ", "");

        // Извлекаем имя пользователя (или email) из токена
        String username = jwtProvider.getAccessTokenUsername(token);
        System.out.println("Authenticated user: " + username);

        // Получаем пользователя по email
        UserEntity user = userService.findUserByEmail(username).orElseThrow(() -> {
            new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return null;
        }); // Предполагаем, что у вас есть метод для поиска пользователя по email

        // Привязываем найденного пользователя к событию
        event.setUser(user);

        // Сохраняем событие
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

