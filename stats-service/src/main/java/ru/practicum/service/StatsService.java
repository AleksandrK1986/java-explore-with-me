package ru.practicum.service;

import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    Hit create(Hit hit);

    List<ViewStats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
