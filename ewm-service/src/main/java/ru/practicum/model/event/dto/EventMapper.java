package ru.practicum.model.event.dto;

import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.Location;
import ru.practicum.model.user.dto.UserDto;

import static ru.practicum.model.category.dto.CategoryMapper.toCategoryDto;
import static ru.practicum.model.user.dto.UserMapper.toUserDto;

public class EventMapper {

    public static EventDto toEventDto(Event event) {
        UserDto userDto = toUserDto(event.getInitiator());
        Location location = new Location(event.getLat(), event.getLon());
        CategoryDto categoryDto = toCategoryDto(event.getCategory());
        return new EventDto(
                event.getId(),
                categoryDto,
                event.getTitle(),
                event.getAnnotation(),
                event.getDescription(),
                event.getEventDate(),
                userDto,
                event.getCreatedOn(),
                event.getPublishedOn(),
                event.getState(),
                event.getPaid(),
                event.getRequestModeration(),
                event.getParticipantLimit(),
                location,
                0,
                0
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        UserDto userDto = toUserDto(event.getInitiator());
        CategoryDto categoryDto = toCategoryDto(event.getCategory());
        return new EventShortDto(
                event.getId(),
                categoryDto,
                event.getTitle(),
                event.getAnnotation(),
                event.getEventDate(),
                userDto,
                event.getPaid(),
                0,
                0
        );
    }

    public static Event toEvent(NewEventDto eventDto) {
        Category category = new Category();
        return new Event(
                0,
                category,
                eventDto.getTitle(),
                eventDto.getAnnotation(),
                eventDto.getDescription(),
                eventDto.getEventDate(),
                null,
                null,
                null,
                null,
                eventDto.getPaid(),
                eventDto.getRequestModeration(),
                eventDto.getParticipantLimit(),
                eventDto.getLocation().getLat(),
                eventDto.getLocation().getLon(),
                0
        );
    }

    public static Event toEvent(UpdateEventDto eventDto) {
        return new Event(
                eventDto.getEventId(),
                null,
                eventDto.getTitle() != null ? eventDto.getTitle() : null,
                eventDto.getAnnotation() != null ? eventDto.getAnnotation() : null,
                eventDto.getDescription() != null ? eventDto.getDescription() : null,
                eventDto.getEventDate() != null ? eventDto.getEventDate() : null,
                null,
                null,
                null,
                null,
                eventDto.getPaid(),
                null,
                eventDto.getParticipantLimit() != 0 ? eventDto.getParticipantLimit() : 0,
                0,
                0,
                0
        );
    }

    public static Event toEvent(AdminEventDto eventDto) {
        float lat = 0;
        float lon = 0;
        if (eventDto.getLocation() != null) {
            lat = eventDto.getLocation().getLat();
            lon = eventDto.getLocation().getLon();
        }
        return new Event(
                0,
                null,
                eventDto.getTitle() != null ? eventDto.getTitle() : null,
                eventDto.getAnnotation() != null ? eventDto.getAnnotation() : null,
                eventDto.getDescription() != null ? eventDto.getDescription() : null,
                eventDto.getEventDate() != null ? eventDto.getEventDate() : null,
                null,
                null,
                null,
                null,
                eventDto.getPaid(),
                eventDto.getRequestModeration() != null ? eventDto.getRequestModeration() : null,
                eventDto.getParticipantLimit() != 0 ? eventDto.getParticipantLimit() : 0,
                lat,
                lon,
                0
        );
    }
}
