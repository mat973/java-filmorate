package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Event {
    private Long userId;
    private Long timestamp;
    private EventType eventType;
    private Operation operation;
    private Long eventId;
    private Long entityId;


}