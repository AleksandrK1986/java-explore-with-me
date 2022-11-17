package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.event.dto.AdminEventDto;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.service.CategoryService;
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;
import ru.practicum.stats.StatsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ru.practicum.model.event.dto.EventMapper.toEvent;
import static ru.practicum.model.event.dto.EventMapper.toEventDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/events")
public class AdminEventController {

    private EventService eventService;
    private CategoryService categoryService;
    private RequestService requestService;
    private StatsService statsService;

    @Autowired
    public AdminEventController(EventService eventService,
                                CategoryService categoryService,
                                RequestService requestService,
                                StatsService statsService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.requestService = requestService;
        this.statsService = statsService;
    }

    @GetMapping
    public List<EventDto> findEventsAdmin(@RequestParam(required = false) Set<Long> users,
                                          @RequestParam(required = false) Set<EventState> states,
                                          @RequestParam(required = false) Set<Long> categories,
                                          @RequestParam(required = false) String rangeStart,
                                          @RequestParam(required = false) String rangeEnd,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Controller: find events by admin with users: {}, states: {}, " +
                "categories: {}, rangeStart: {}, rangeEnd: {}", users, states, categories, rangeStart, rangeEnd);
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        }
        List<Event> events = eventService.findEventsAdmin(users, states, categories, start, end, from, size);
        List<EventDto> eventDtos = new ArrayList<>();
        for (Event e : events) {
            EventDto eventDto = toEventDto(e);
            eventDto.setConfirmedRequests(requestService.getAmountConfirms(e.getId()));
            eventDtos.add(eventDto);
            Long views = statsService.getViews("/events/" + eventDto.getId());
            eventDto.setViews(views);
        }
        log.info("Controller: return events by admin with event: {}", eventDtos);
        return eventDtos;
    }

    @PutMapping("/{eventId}")
    public EventDto updateAdmin(@PathVariable long eventId, @RequestBody AdminEventDto eventDto) {
        log.info("Controller: admin update event with eventId={}, eventDto: {}", eventId, eventDto);
        Event event = toEvent(eventDto);
        event.setId(eventId);
        if (eventDto.getCategoryId() != 0) {
            event.setCategory(categoryService.findById(eventDto.getCategoryId()));
        }
        EventDto returnEvent = toEventDto(eventService.updateAdmin(event));
        returnEvent.setConfirmedRequests(requestService.getAmountConfirms(returnEvent.getId()));
        Long views = statsService.getViews("/events/" + returnEvent.getId());
        returnEvent.setViews(views);
        log.info("Controller: return after admin updating event with event: {}", returnEvent);
        return returnEvent;
    }

    @PatchMapping("/{eventId}/publish")
    public EventDto publish(@PathVariable long eventId) {
        log.info("Controller: admin publish event with eventId={}", eventId);
        EventDto returnEvent = toEventDto(eventService.publish(eventId));
        returnEvent.setConfirmedRequests(requestService.getAmountConfirms(returnEvent.getId()));
        Long views = statsService.getViews("/events/" + returnEvent.getId());
        returnEvent.setViews(views);
        log.info("Controller: return after admin publishing event: {}", returnEvent);
        return returnEvent;
    }

    @PatchMapping("/{eventId}/reject")
    public EventDto reject(@PathVariable long eventId) {
        log.info("Controller: admin reject event with eventId={}", eventId);
        EventDto returnEvent = toEventDto(eventService.reject(eventId));
        returnEvent.setConfirmedRequests(requestService.getAmountConfirms(returnEvent.getId()));
        Long views = statsService.getViews("/events/" + returnEvent.getId());
        returnEvent.setViews(views);
        log.info("Controller: return after admin rejecting event: {}", returnEvent);
        return returnEvent;
    }


}
