package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT new ru.practicum.model.ViewStats(app, uri, COUNT(DISTINCT ip)) " +
            "FROM Hit " +
            "WHERE uri IN :uris AND (timestamp >= :start AND timestamp < :end) GROUP BY app, uri"
    )
    List<ViewStats> calculateUniqueStats(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.model.ViewStats(app, uri, COUNT(ip)) " +
            "FROM Hit " +
            "WHERE uri IN :uris AND (timestamp >= :start AND timestamp <= :end) GROUP BY app, uri"
    )
    List<ViewStats> calculateStats(List<String> uris, LocalDateTime start, LocalDateTime end);

}
