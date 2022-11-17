package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventSort;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.service.CategoryService;
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;
import ru.practicum.stats.StatsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ru.practicum.model.event.dto.EventMapper.toEventDto;
import static ru.practicum.model.event.dto.EventMapper.toEventShortDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events")
public class PublicEventController {
    private EventService eventService;
    private CategoryService categoryService;
    private RequestService requestService;
    private StatsService statsService;

    @Autowired
    public PublicEventController(EventService eventService,
                                 CategoryService categoryService,
                                 RequestService requestService,
                                 StatsService statsService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.requestService = requestService;
        this.statsService = statsService;
    }

    @GetMapping
    public List<EventShortDto> findEvents(@RequestParam(defaultValue = "") String text,
                                          @RequestParam(required = false) Set<Long> categories,
                                          @RequestParam(required = false) Boolean paid,
                                          @RequestParam(required = false) String rangeStart,
                                          @RequestParam(required = false) String rangeEnd,
                                          @RequestParam(required = false) Boolean onlyAvailable,
                                          @RequestParam(required = false) EventSort sort,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size,
                                          HttpServletRequest request) {
        log.info("Controller: find all events with text={}, paid={}, rangeStar={}, rangeEnd={}, " +
                        "onlyAvailable={}, sort={}, from={}, siz={}, compilations: {}", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        }
        List<Event> events = eventService.findEvents(text, categories, paid, start, end, onlyAvailable,
                sort, from, size);
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event e : events) {
            EventShortDto eventShortDto = toEventShortDto(e);
            eventShortDto.setConfirmedRequests(requestService.getAmountConfirms(e.getId()));
            Long views = statsService.getViews("/events/" + eventShortDto.getId());
            eventShortDto.setViews(views);
            eventShortDtos.add(eventShortDto);
        }
        statsService.setHits(request.getRequestURI(), request.getRemoteAddr());
        log.info("Controller: return all events: {}", eventShortDtos);
        return eventShortDtos;
    }

    @GetMapping("/{eventId}")
    public EventDto findEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Controller: find event with eventId={}", eventId);
        EventDto eventDto = toEventDto(eventService.findById(eventId));
        eventDto.setConfirmedRequests(requestService.getAmountConfirms(eventId));
        Long views = statsService.getViews("/events/" + eventDto.getId());
        eventDto.setViews(views);
        statsService.setHits(request.getRequestURI(), request.getRemoteAddr());
        log.info("Controller: return event with eventId={}", eventDto);
        return eventDto;
    }

}
