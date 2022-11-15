package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.errors.exception.ForbiddenException;
import ru.practicum.errors.exception.NotFoundException;

import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.request.ParticipationRequest;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RequestServiceImp implements RequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public RequestServiceImp(RequestRepository requestRepository,
                             EventRepository eventRepository,
                             UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ParticipationRequest> findByEvent(Event event) {
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(event.getId());
        log.info("Service: find requests by Event:{}", requests);
        return requests;
    }

    @Override
    public ParticipationRequest confirm(Event event, long reqId) {
        ParticipationRequest request = checkAndGetRequest(reqId);
        long participants = 0;
        if (event.getParticipantLimit() != 0) {
            participants = getAmountConfirms(event.getId());
            if (participants > event.getParticipantLimit()) {
                throw new ForbiddenException("Превышен лимит запросов на участие");
            }
        }
        request.setStatus(RequestStatus.CONFIRMED);
        request = requestRepository.save(request);
        participants++;
        if (participants >= event.getParticipantLimit()) {
            List<ParticipationRequest> rejectedRequests = requestRepository.findAllByEventIdAndStatus(event.getId(),
                    RequestStatus.PENDING);
            for (ParticipationRequest r : rejectedRequests) {
                r.setStatus(RequestStatus.REJECTED);
            }
            requestRepository.saveAll(rejectedRequests);
        }
        log.info("Service: confirm request reqId={}: {}", reqId, request);
        return request;
    }

    @Override
    public ParticipationRequest reject(long reqId) {
        ParticipationRequest request = checkAndGetRequest(reqId);
        request.setStatus(RequestStatus.REJECTED);
        request = requestRepository.save(request);
        log.info("Service: reject request reqId={}: {}", reqId, request);
        return request;
    }

    @Override
    public ParticipationRequest create(long userId, long eventId) {
        User user = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);
        if (event.getInitiator().getId() == userId) {
            throw new ForbiddenException("Нельзя добавить запрос на участие в своём событии");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ForbiddenException("Нельзя заявиться на участие в неопубликованном событии");
        }
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId) != null) {
            throw new ForbiddenException("Нельзя добавить повторный запрос");
        }
        if (event.getParticipantLimit() != 0) {
            if (getAmountConfirms(eventId) > event.getParticipantLimit()) {
                throw new ForbiddenException("Достигнут лимит запросов на участие");
            }
        }
        ParticipationRequest request = new ParticipationRequest(0, LocalDateTime.now(), event, user,
                RequestStatus.PENDING.PENDING);
        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        log.info("Service: create request from userId={}", userId);
        return requestRepository.save(request);
    }

    @Override
    public List<ParticipationRequest> findByUserId(long userId) {
        checkAndGetUser(userId);
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Service: return requests from userId={} participationRequestDtos: {}", userId, requests);
        return requests;
    }

    @Override
    public ParticipationRequest cancel(long userId, long reqId) {
        checkAndGetUser(userId);
        ParticipationRequest request = checkAndGetRequest(reqId);
        if (request.getRequester().getId() == userId) {
            request.setStatus(RequestStatus.CANCELED);
        } else {
            throw new ForbiddenException("Пользователь ID=" + userId + " не подавал заявку  ID=" + reqId);
        }
        log.info("Service: canceling requestId={} from userId={}", reqId, userId);
        return requestRepository.save(request);
    }

    private User checkAndGetUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден в хранилище");
        }
        return userRepository.getReferenceById(userId);
    }

    private Event checkAndGetEvent(long eventId) {
        if (eventRepository.findById(eventId).isEmpty()) {
            throw new NotFoundException("Событие не найден в хранилище");
        }
        return eventRepository.getReferenceById(eventId);
    }

    private ParticipationRequest checkAndGetRequest(long requestId) {
        if (requestRepository.findById(requestId).isEmpty()) {
            throw new NotFoundException("Запрос не найден в хранилище");
        }
        return requestRepository.getReferenceById(requestId);
    }

    @Override
    public int getAmountConfirms(long eventId) {
        return requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size();
    }
}
