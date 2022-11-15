package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.compilation.dto.NewCompilationDto;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;

import javax.validation.Valid;

import static ru.practicum.model.compilation.dto.CompilationMapper.toCompilation;
import static ru.practicum.model.compilation.dto.CompilationMapper.toCompilationDto;
import static ru.practicum.model.event.dto.EventMapper.toEventShortDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {

    private CompilationService compilationService;
    private EventService eventService;

    @Autowired
    public AdminCompilationController(CompilationService compilationService,
                                      EventService eventService) {
        this.compilationService = compilationService;
        this.eventService = eventService;
    }

    @PostMapping
    public CompilationDto create(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Controller: create compilation {}", compilationDto);
        Compilation compilation = toCompilation(compilationDto);
        for (int i : compilationDto.getEvents()) {
            compilation.addEvent(eventService.findById(i));
        }
        CompilationDto returnCompilation = toCompilationDto(compilationService.create(compilation));
        for (int i : compilationDto.getEvents()) {
            returnCompilation.addEvent(toEventShortDto(eventService.findById(i)));
        }
        log.info("Controller: return category {}", returnCompilation);
        return returnCompilation;
    }

    @DeleteMapping("/{compId}")
    public void delete(@PathVariable long compId) {
        log.info("Controller: delete compilation with id {}", compId);
        compilationService.delete(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable long compId,
                            @PathVariable long eventId) {
        log.info("Controller: delete event from compilation with compId={}, event={}", compId, eventId);
        compilationService.deleteEvent(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEvent(@PathVariable long compId,
                         @PathVariable long eventId) {
        log.info("Controller: add event in compilation with compId={}, event={}", compId, eventId);
        compilationService.addEvent(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unPin(@PathVariable long compId) {
        log.info("Controller: unPin compilation with compId={}", compId);
        compilationService.unPin(compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pin(@PathVariable long compId) {
        log.info("Controller: pin compilation with compId={}", compId);
        compilationService.pin(compId);
    }

}
