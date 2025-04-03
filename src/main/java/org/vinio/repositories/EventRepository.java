package org.vinio.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vinio.entities.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserId(Long userId);
    List<Event> findAllByUserEmail (String UserEmail);
}
