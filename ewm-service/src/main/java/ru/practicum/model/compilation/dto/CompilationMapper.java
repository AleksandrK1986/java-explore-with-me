package ru.practicum.model.compilation.dto;

import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventShortDto;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto compilationDto) {
        List<Event> events = new ArrayList<>();
        return new Compilation(
                0,
                compilationDto.getTitle(),
                compilationDto.getPinned(),
                events
        );
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> events = new ArrayList<>();
        return new CompilationDto(
                compilation.getId(),
                events,
                compilation.getTitle(),
                compilation.getPinned()
        );
    }
}
