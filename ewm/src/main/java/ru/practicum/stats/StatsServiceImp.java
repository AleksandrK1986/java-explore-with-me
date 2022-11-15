package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.client.dto.EndpointHit;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImp implements StatsService {
    private final StatsClient statsClient;

    @Override
    public Long getViews(String uri) {
        String start = LocalDateTime.ofEpochSecond(0, 0,
                ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = LocalDateTime.now().plusYears(100).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<ViewStats> viewStats = statsClient.findStats(start, end, uri, false);
        if (viewStats.isEmpty()) {
            return 0L;
        }
        return viewStats.get(0).getHits();
    }

    @Override
    public void setHits(String uri, String ip) {
        EndpointHit endpointHit = new EndpointHit(null, "ewm-service", uri, ip, LocalDateTime.now());
        statsClient.create(endpointHit);
    }
}
