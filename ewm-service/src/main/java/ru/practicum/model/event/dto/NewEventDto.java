package ru.practicum.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.event.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NewEventDto {
    @NotNull
    private long category;
    @NotNull
    @Size(min = 3, max = 120)
    private String title;
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Boolean paid;
    private Boolean requestModeration;
    private int participantLimit;
    private Location location;

    @Override
    public String toString() {
        return "NewEventDto{" +
                "categoryId=" + category +
                ", title='" + title + '\'' +
                ", annotation='" + annotation + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", paid=" + paid +
                ", requestModeration=" + requestModeration +
                ", participantLimit=" + participantLimit +
                ", location=" + location +
                '}';
    }
}
