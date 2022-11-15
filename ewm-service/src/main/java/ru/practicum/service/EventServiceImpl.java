package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.errors.exception.ForbiddenException;
import ru.practicum.errors.exception.NotValidException;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventSort;
import ru.practicum.model.event.EventState;
import ru.practicum.model.request.ParticipationRequest;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private EntityManager entityManager;
    private RequestService requestService;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            UserRepository userRepository,
                            EntityManager entityManager,
                            RequestService requestService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.requestService = requestService;
    }

    @Override
    public Event create(long userId, Event event) {
        checkUser(userId);
        event.setInitiator(userRepository.getReferenceById(userId));
        Event newEvent = eventRepository.save(event);
        log.info("Service: create event {}", newEvent);
        return newEvent;
    }

    @Override
    public Event update(long userId, Event data) {
        checkUser(userId);
        Event eventFromDb = eventRepository.getReferenceById(data.getId());
        checkInitiatorEvent(userId, eventFromDb);
        if (eventFromDb.getState() == EventState.PUBLISHED) {
            throw new NotValidException("Изменить можно только отмененные события " +
                    "или события в состоянии ожидания модерации");
        }
        if (!LocalDateTime.now().isBefore(eventFromDb.getEventDate().minusHours(2))) {
            throw new NotValidException("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента");
        }
        if (data.getCategory() != null) {
            eventFromDb.setCategory(data.getCategory());
        }
        if (data.getTitle() != null) {
            eventFromDb.setTitle(data.getTitle());
        }
        if (data.getAnnotation() != null) {
            eventFromDb.setAnnotation(data.getAnnotation());
        }
        if (data.getDescription() != null) {
            eventFromDb.setDescription(data.getDescription());
        }
        if (data.getEventDate() != null) {
            eventFromDb.setEventDate(data.getEventDate());
        }
        if (data.getPaid() != null) {
            eventFromDb.setPaid(data.getPaid());
        }
        if (data.getParticipantLimit() != 0) {
            eventFromDb.setParticipantLimit(data.getParticipantLimit());
        }
        log.info("Service: update event {}", eventFromDb);
        return eventRepository.save(eventFromDb);
    }

    @Override
    public Event updateAdmin(Event data) {
        Event eventFromDb = eventRepository.getReferenceById(data.getId());
        if (data.getCategory() != null) {
            eventFromDb.setCategory(data.getCategory());
        }
        if (data.getTitle() != null) {
            eventFromDb.setTitle(data.getTitle());
        }
        if (data.getAnnotation() != null) {
            eventFromDb.setAnnotation(data.getAnnotation());
        }
        if (data.getDescription() != null) {
            eventFromDb.setDescription(data.getDescription());
        }
        if (data.getEventDate() != null) {
            eventFromDb.setEventDate(data.getEventDate());
        }
        if (data.getPaid() != null) {
            eventFromDb.setPaid(data.getPaid());
        }
        if (data.getParticipantLimit() != 0) {
            eventFromDb.setParticipantLimit(data.getParticipantLimit());
        }
        if (data.getLat() != 0) {
            eventFromDb.setLat(data.getLat());
            eventFromDb.setLon(data.getLat());
        }
        if (data.getRequestModeration() != null) {
            eventFromDb.setRequestModeration(data.getRequestModeration());
        }
        log.info("Service: admin update event {}", eventFromDb);
        return eventRepository.save(eventFromDb);
    }

    @Override
    public Event findById(long userId, long eventId) {
        checkUser(userId);
        Event eventFromDb = eventRepository.getReferenceById(eventId);
        checkInitiatorEvent(userId, eventFromDb);
        log.info("Service: get event {}", eventFromDb);
        return eventFromDb;
    }

    @Override
    public Event findById(long eventId) {
        Event eventFromDb = eventRepository.getReferenceById(eventId);
        log.info("Service: get event by id {}", eventFromDb);
        return eventFromDb;
    }

    @Override
    public Event cancel(long userId, long eventId) {
        checkUser(userId);
        Event eventFromDb = eventRepository.getReferenceById(eventId);
        checkInitiatorEvent(userId, eventFromDb);
        if (eventFromDb.getState() != EventState.PENDING) {
            throw new NotValidException("Отменить можно только событие в состоянии ожидания модерации");
        }
        eventFromDb.setState(EventState.CANCELED);
        log.info("Service: cancel event {}", eventFromDb);
        return eventRepository.save(eventFromDb);
    }

    @Override
    public Event publish(long eventId) {
        Event eventFromDb = eventRepository.getReferenceById(eventId);
        if (!LocalDateTime.now().isBefore(eventFromDb.getEventDate().minusHours(1))) {
            throw new NotValidException("Дата начала события должна быть не ранее чем за час от даты публикации");
        }
        if (eventFromDb.getState() != EventState.PENDING) {
            throw new NotValidException("Cобытие должно быть в состоянии ожидания публикации");
        }
        eventFromDb.setState(EventState.PUBLISHED);
        log.info("Service: publish event {}", eventFromDb);
        return eventRepository.save(eventFromDb);
    }

    @Override
    public Event reject(long eventId) {
        Event eventFromDb = eventRepository.getReferenceById(eventId);
        if (eventFromDb.getState() == EventState.PUBLISHED) {
            throw new NotValidException("Cобытие не должно быть опубликовано");
        }
        eventFromDb.setState(EventState.CANCELED);
        log.info("Service: reject event {}", eventFromDb);
        return eventRepository.save(eventFromDb);
    }

    @Override
    public List<Event> findEvents(long userId, int from, int size) {
        checkUser(userId);
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page;
        if (from != 0) {
            page = PageRequest.of(from / size, from / size, sortBy);
        } else {
            page = PageRequest.of(0, size, sortBy);
        }
        User user = userRepository.getReferenceById(userId);
        Page<Event> events = eventRepository.findEventsByInitiatorOrderById(user, page);
        log.info("Service: find events, with from={}, size={}", from, size);
        return events.getContent();
    }

    @Override
    public List<ParticipationRequest> findRequests(long userId, long eventId) {
        checkUser(userId);
        Event event = eventRepository.getReferenceById(eventId);
        log.info("Service: find request, with userId={}, eventId={}", userId, eventId);
        return requestService.findByEvent(event);
    }

    @Override
    public ParticipationRequest confirm(long userId, long eventId, long reqId) {
        checkUser(userId);
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new ForbiddenException("Запрос на участие может подтверждать только инициатор");
        }
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ForbiddenException("Нельзя подтверждать участие в неопубликованном событии");
        }
        log.info("Service: confirm request, with userId={}, eventId={}, reqId={}", userId, eventId, reqId);
        return requestService.confirm(event, reqId);
    }

    @Override
    public ParticipationRequest reject(long userId, long eventId, long reqId) {
        checkUser(userId);
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new ForbiddenException("Запрос на участие может отклонять только инициатор");
        }
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ForbiddenException("Нельзя отклонять участие в неопубликованном событии");
        }
        log.info("Service: reject request, with userId={}, eventId={}, reqId={}", userId, eventId, reqId);
        return requestService.reject(reqId);
    }

    private void setFilters(Session session, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean paid) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(999);
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new NotValidException("Дата и время окончаний события не может быть раньше даты начала событий");
        }

        Filter dateFilter = session.enableFilter("dateFilter");
        dateFilter.setParameter("rangeStart", rangeStart);
        dateFilter.setParameter("rangeEnd", rangeEnd);

        if (paid != null) {
            session.enableFilter("paidFilter").setParameter("paid", paid);
        }
    }

    private void disableFilters(Session session) {
        session.disableFilter("dateFilter");
        session.disableFilter("stateFilter");
        session.disableFilter("paidFilter");
    }

    @Override
    public List<Event> findEvents(String text, Set<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from,
                                  Integer size) {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("stateFilter").setParameter("state", EventState.PUBLISHED.toString());
        setFilters(session, rangeStart, rangeEnd, paid);
        List<Event> events;
        if (categories != null) {
            events = eventRepository.findByCategoryIdsAndText(categories, text);
        } else {
            events = eventRepository.findByText(text);
        }
        disableFilters(session);

        if (onlyAvailable != null && onlyAvailable) {
            events = events.stream()
                    .filter(x -> requestService.getAmountConfirms(x.getId()) < x.getParticipantLimit())
                    .collect(toList());
        }

        if (EventSort.VIEWS.equals(sort)) {
            events = events.stream()
                    .sorted(Comparator.comparingLong(Event::getViews))
                    .skip(from)
                    .limit(size)
                    .collect(toList());
        } else {
            events = events.stream()
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .skip(from)
                    .limit(size)
                    .collect(toList());
        }
        log.info("Service: find events with text: {}, categories: {}, " +
                        "paid: {}, rangeStart: {}, rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return events;
    }


    @Override
    public List<Event> findEventsAdmin(Set<Long> users, Set<EventState> states, Set<Long> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                       Integer size) {
        if (states == null) {
            states = Set.of(EventState.PENDING, EventState.CANCELED, EventState.PUBLISHED);
        }
        Session session = entityManager.unwrap(Session.class);
        setFilters(session, rangeStart, rangeEnd, null);

        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page;
        if (from != 0) {
            page = PageRequest.of(from / size, from / size, sortBy);
        } else {
            page = PageRequest.of(0, size, sortBy);
        }

        List<Event> events;
        if (users != null && categories != null) {
            events = eventRepository.findByUsersAndCategoriesAndStates(users, categories, states, page);
        } else if (users == null && categories == null) {
            events = eventRepository.findByStates(states, page);
        } else if (users == null) {
            events = eventRepository.findByCategoriesAndStates(categories, states, page);
        } else {
            events = eventRepository.findByUsersAndStates(users, states, page);
        }

        session.disableFilter("dateFilter");
        log.info("Service: find events by admin with users: {}, states: {}, " +
                "categories: {}, rangeStart: {}, rangeEnd: {}", users, states, categories, rangeStart, rangeEnd);
        return events;
    }

    private void checkInitiatorEvent(long userId, Event event) {
        if (event.getInitiator() != userRepository.getReferenceById(userId)) {
            throw new NotValidException("Пользователь  не может получать или изменять чужое событие");
        }
    }

    private void checkUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ForbiddenException("Пользователь не найден в хранилище");
        }
    }

}
