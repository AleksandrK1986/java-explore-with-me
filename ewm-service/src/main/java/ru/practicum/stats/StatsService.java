package ru.practicum.stats;


public interface StatsService {

    void setHits(String uri, String ip);

    Long getViews(String uri);
}
