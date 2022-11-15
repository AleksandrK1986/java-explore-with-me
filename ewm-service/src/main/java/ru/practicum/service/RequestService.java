package ru.practicum.service;

import ru.practicum.model.event.Event;
import ru.practicum.model.request.ParticipationRequest;

import java.util.List;

public interface RequestService {
    List<ParticipationRequest> findByEvent(Event event);

    ParticipationRequest confirm(Event event, long reqId);

    ParticipationRequest reject(long reqId);

    ParticipationRequest create(long userId, long eventId);

    List<ParticipationRequest> findByUserId(long userId);

    ParticipationRequest cancel(long userId, long requestId);

    int getAmountConfirms(long eventId);
}
