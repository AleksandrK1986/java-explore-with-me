package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public List<ViewStats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            return statsRepository.calculateUniqueStats(uris, start, end);
        }
        return statsRepository.calculateStats(uris, start, end);
    }

    @Override
    public Hit create(Hit hit) {
        Hit hitFromDb = statsRepository.save(hit);
        return hitFromDb;
    }


}
