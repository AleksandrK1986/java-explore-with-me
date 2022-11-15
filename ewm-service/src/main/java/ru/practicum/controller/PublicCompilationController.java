package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.event.Event;
import ru.practicum.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.model.compilation.dto.CompilationMapper.toCompilationDto;
import static ru.practicum.model.event.dto.EventMapper.toEventShortDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/compilations")
public class PublicCompilationController {

    private CompilationService compilationService;

    @Autowired
    public PublicCompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    public List<CompilationDto> findAll(@RequestParam(name = "pinned", defaultValue = "false") Boolean pinned,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<Compilation> compilations = compilationService.findAll(pinned, from, size);
        List<CompilationDto> compilationsDtos = new ArrayList<>();
        for (Compilation c : compilations) {
            CompilationDto compilationDto = toCompilationDto(c);
            for (Event e : c.getEvents()) {
                compilationDto.addEvent(toEventShortDto(e));
            }
            compilationsDtos.add(compilationDto);
        }
        log.info("Controller: find all with pinned={}, from={}, siz={}, compilations: {}", pinned, from, size, compilations);
        return compilationsDtos;
    }

    @GetMapping("/{compId}")
    public CompilationDto findById(@PathVariable long compId) {
        Compilation compilation = compilationService.findById(compId);
        CompilationDto compilationDto = toCompilationDto(compilation);
        for (Event e : compilation.getEvents()) {
            compilationDto.addEvent(toEventShortDto(e));
        }
        log.info("Controller: find compilation by compId {}", compId);
        return compilationDto;
    }
}
