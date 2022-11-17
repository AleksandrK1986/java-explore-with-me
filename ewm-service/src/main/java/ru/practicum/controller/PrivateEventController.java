package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.event.dto.EventDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.NewEventDto;
import ru.practicum.model.event.dto.UpdateEventDto;
import ru.practicum.model.request.ParticipationRequest;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.service.CategoryService;
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;
import ru.practicum.service.UserService;
import ru.practicum.stats.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.model.event.dto.EventMapper.*;
import static ru.practicum.model.request.dto.RequestMapper.toParticipationRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {

    private EventService eventService;
    private CategoryService categoryService;
    private UserService userService;
    private RequestService requestService;
    private StatsService statsService;

    @Autowired
    public PrivateEventController(EventService eventService,
                                  CategoryService categoryService,
                                  UserService userService,
                                  RequestService requestService,
                                  StatsService statsService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.requestService = requestService;
        this.statsService = statsService;
    }

    @PostMapping
    public EventDto create(@Valid @RequestBody NewEventDto eventDto,
                           @PathVariable(name = "userId") long userId) {
        log.info("Controller: post event with userId={}, newEventDto: {}", userId, eventDto);
        Event event = toEvent(eventDto);
        event.setCategory(categoryService.findById(eventDto.getCategory()));
        event.setInitiator(userService.findById(userId));
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        log.info("Controller: create event with userId={}, event: {}", userId, event);
        EventDto returnEvent = toEventDto(eventService.create(userId, event));
        returnEvent.setConfirmedRequests(requestService.getAmountConfirms(returnEvent.getId()));
        log.info("Controller: return after creating event with userId={}, event: {}", userId, returnEvent);
        return returnEvent;
    }

    @PatchMapping
    public EventDto update(@Valid @RequestBody UpdateEventDto eventDto,
                           @PathVariable(name = "userId") long userId) {
        log.info("Controller: update event with userId={}, updateEventDto: {}", userId, eventDto);
        Event event = toEvent(eventDto);
        if (eventDto.getCategoryId() != 0) {
            event.setCategory(categoryService.findById(eventDto.getCategoryId()));
        }
        log.info("Controller: update event with userId={}, event: {}", userId, event);
        EventDto returnEvent = toEventDto(eventService.update(userId, event));
        returnEvent.setConfirmedRequests(requestService.getAmountConfirms(returnEvent.getId()));
        Long views = statsService.getViews("/events/" + returnEvent.getId());
        returnEvent.setViews(views);
        log.info("Controller: return after updating event with userId={}, event: {}", userId, returnEvent);
        return returnEvent;
    }

    @GetMapping("/{eventId}")
    public EventDto findById(@PathVariable(name = "userId") long userId,
                             @PathVariable(name = "eventId") long eventId) {
        log.info("Controller: get event with userId={}, eventId={}", userId, eventId);
        EventDto returnEvent = toEventDto(eventService.findById(userId, eventId));
        returnEvent.setConfirmedRequests(requestService.getAmountConfirms(returnEvent.getId()));
        Long views = statsService.getViews("/events/" + returnEvent.getId());
        returnEvent.setViews(views);
        log.info("Controller: return after getting event with userId={}, event: {}", userId, returnEvent);
        return returnEvent;
    }

    @PatchMapping("/{eventId}")
    public EventDto cancel(@PathVariable(name = "userId") long userId,
                           @PathVariable(name = "eventId") long eventId) {
        log.info("Controller: cancel event with userId={}, eventId={}", userId, eventId);
        EventDto returnEvent = toEventDto(eventService.cancel(userId, eventId));
        returnEvent.setConfirmedRequests(requestService.getAmountConfirms(returnEvent.getId()));
        Long views = statsService.getViews("/events/" + returnEvent.getId());
        returnEvent.setViews(views);
        log.info("Controller: return after canceling event with userId={}, event: {}", userId, returnEvent);
        return returnEvent;
    }

    @GetMapping
    public List<EventShortDto> findEvents(@PathVariable long userId,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "100") Integer size) {
        List<Event> events = eventService.findEvents(userId, from, size);
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event e : events) {
            EventShortDto eventShortDto = toEventShortDto(e);
            eventShortDto.setConfirmedRequests(requestService.getAmountConfirms(eventShortDto.getId()));
            Long views = statsService.getViews("/events/" + eventShortDto.getId());
            eventShortDto.setViews(views);
            eventShortDtos.add(eventShortDto);
        }
        log.info("Get all events with userId={}, from={}, size={}: {}", userId, from, size, eventShortDtos);
        return eventShortDtos;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findRequests(@PathVariable long userId,
                                                      @PathVariable long eventId) {
        log.info("Get all requests with userId={}, eventId={}", userId, eventId);
        List<ParticipationRequest> requests = eventService.findRequests(userId, eventId);
        List<ParticipationRequestDto> requestDtos = new ArrayList<>();
        for (ParticipationRequest r : requests) {
            requestDtos.add(toParticipationRequestDto(r));
        }
        log.info("Controller: return requests with userId={}, eventId={}: {}", userId, eventId, requestDtos);
        return requestDtos;
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirm(@PathVariable long userId,
                                           @PathVariable long eventId,
                                           @PathVariable long reqId) {
        log.info("Controller: confirm requests with userId={}, eventId={}, reqId={}", userId, eventId, reqId);
        return toParticipationRequestDto(eventService.confirm(userId, eventId, reqId));
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto reject(@PathVariable long userId,
                                          @PathVariable long eventId,
                                          @PathVariable long reqId) {
        log.info("Controller: confirm requests with userId={}, eventId={}, reqId={}", userId, eventId, reqId);
        return toParticipationRequestDto(eventService.reject(userId, eventId, reqId));
    }

}
