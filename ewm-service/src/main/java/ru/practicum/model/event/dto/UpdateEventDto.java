package ru.practicum.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateEventDto {
    private long eventId;
    private long categoryId;

    @Size(min = 1, max = 255)
    private String title;
    @Size(min = 1, max = 1000)
    private String annotation;
    @Size(min = 1, max = 1000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Boolean paid;
    private int participantLimit;

    @Override
    public String toString() {
        return "UpdateEventDto{" +
                "id=" + eventId +
                ", categoryId=" + categoryId +
                ", title='" + title + '\'' +
                ", annotation='" + annotation + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", paid=" + paid +
                ", participantLimit=" + participantLimit +
                '}';
    }
}
