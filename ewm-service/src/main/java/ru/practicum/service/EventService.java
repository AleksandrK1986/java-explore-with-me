package ru.practicum.service;

import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventSort;
import ru.practicum.model.event.EventState;
import ru.practicum.model.request.ParticipationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {

    List<Event> findEvents(long userId, int from, int size);

    Event create(long userId, Event data);

    Event update(long userId, Event data);

    Event updateAdmin(Event data);

    Event publish(long eventId);

    Event reject(long eventId);

    Event findById(long userId, long eventId);

    Event findById(long eventId);

    Event cancel(long userId, long data);

    List<ParticipationRequest> findRequests(long userId, long eventId);

    ParticipationRequest confirm(long userId, long eventId, long reqId);

    ParticipationRequest reject(long userId, long eventId, long reqId);

    List<Event> findEventsAdmin(Set<Long> users, Set<EventState> states, Set<Long> categories, LocalDateTime parse,
                                LocalDateTime parse1, Integer from, Integer size);

    List<Event> findEvents(String text, Set<Long> categories, Boolean paid, LocalDateTime rangeStart,
                           LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from,
                           Integer size);

}
