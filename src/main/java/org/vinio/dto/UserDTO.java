package org.vinio.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vinio.entities.UserRole;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;

    private String name;

    private String email;

    private String password;

//    private Set<UserRole> roles = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

//    private List<EventDTO> events;
}
