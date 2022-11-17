package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.request.ParticipationRequest;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.model.request.dto.RequestMapper.toParticipationRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    @Autowired
    public PrivateRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public List<ParticipationRequestDto> findByUserId(@PathVariable long userId) {
        log.info("Controller: get requests from userId={}", userId);
        List<ParticipationRequest> requests = requestService.findByUserId(userId);
        List<ParticipationRequestDto> requestsDtos = new ArrayList<>();
        for (ParticipationRequest p : requests) {
            requestsDtos.add(toParticipationRequestDto(p));
        }
        log.info("Controller: return requests from userId={} participationRequestDtos: {}", userId, requestsDtos);
        return requestsDtos;
    }

    @PostMapping
    public ParticipationRequestDto create(@RequestParam long eventId,
                                          @PathVariable long userId) {
        log.info("Controller: create requests from userId={} on eventId={}", userId, eventId);
        return toParticipationRequestDto(requestService.create(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable long userId,
                                          @PathVariable long requestId) {
        log.info("Controller: cancel requestId={} from userId={}", requestId, userId);
        return toParticipationRequestDto(requestService.cancel(userId, requestId));
    }
}
