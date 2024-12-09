package org.vinio.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;

//    private String user;

    private String startTime;

    private String  endTime;

    private String status;

    private String title;

    private String description;

    private Integer calories;

    private String category;

    private String  createdAt;

    private String  updatedAt;

}
