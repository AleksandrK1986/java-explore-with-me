package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.ViewStats;
import ru.practicum.stats.client.dto.EndpointHit;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient {
    private final RestTemplate restTemplate;

    @Autowired
    public StatsClient(@Value("${stats.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void create(EndpointHit endpointHit) {
        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(endpointHit, defaultHeaders());
        restTemplate.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
    }

    public List<ViewStats> findStats(String start, String end, String uri, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uri,
                "unique", unique
        );
        ResponseEntity<ViewStats[]> entity = restTemplate.getForEntity(
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                ViewStats[].class,
                parameters);

        if (entity.getBody() != null) {
            return Arrays.asList(entity.getBody());
        }
        return Collections.emptyList();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

}
