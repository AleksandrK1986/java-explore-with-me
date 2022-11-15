package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.ViewStats;
import ru.practicum.model.dto.EndpointHit;
import ru.practicum.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.model.dto.HitMapper.toEndpointHit;
import static ru.practicum.model.dto.HitMapper.toHit;

@Slf4j
@RestController
public class StatsController {
    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public EndpointHit create(@RequestBody EndpointHit endpointHit, HttpServletRequest request) {
        log.info("Controller: create hit from: {}, to: {}; {}", request.getRemoteAddr(), request.getRequestURI(),
                endpointHit.toString());
        return toEndpointHit(statsService.create(toHit(endpointHit)));
    }

    @GetMapping("/stats")
    public List<ViewStats> findStats(@RequestParam String start,
                                     @RequestParam String end,
                                     @RequestParam List<String> uris,
                                     @RequestParam(defaultValue = "false") boolean unique,
                                     HttpServletRequest request) {
        log.info("Controller: getting statistics from: {}, to: {}", request.getRemoteAddr(), request.getRequestURI());
        return statsService.findStats(LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), uris, unique);
    }
}