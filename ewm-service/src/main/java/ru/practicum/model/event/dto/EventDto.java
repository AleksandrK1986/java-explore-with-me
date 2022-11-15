package ru.practicum.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.EventState;
import ru.practicum.model.user.dto.UserDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EventDto {
    private long id;

    private CategoryDto category;

    private String title;

    private String annotation;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserDto initiator;

    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    private EventState state;

    private Boolean paid;

    private Boolean requestModeration;

    private int participantLimit;

    private Location location;

    private int views;

    private int confirmedRequests;

    @Override
    public String toString() {
        return "EventDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", annotation='" + annotation + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", initiator=" + initiator +
                ", createdOn=" + createdOn +
                ", publishedOn=" + publishedOn +
                ", state=" + state +
                ", paid=" + paid +
                ", requestModeration=" + requestModeration +
                ", participantLimit=" + participantLimit +
                ", location=" + location +
                ", views=" + views +
                '}';
    }
}
