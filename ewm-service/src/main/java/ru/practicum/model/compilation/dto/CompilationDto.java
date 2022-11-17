package ru.practicum.model.compilation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.event.dto.EventShortDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CompilationDto {

    private long id;
    List<EventShortDto> events;
    private String title;
    private Boolean pinned;

    public void addEvent(EventShortDto event) {
        events.add(event);
    }

    @Override
    public String toString() {
        return "CompilationDto{" +
                "id=" + id +
                ", events=" + events +
                ", title='" + title + '\'' +
                ", pinned=" + pinned +
                '}';
    }
}
