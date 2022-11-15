package ru.practicum.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import ru.practicum.model.category.Category;
import ru.practicum.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@FilterDef(name = "paidFilter", parameters = @ParamDef(name = "paid", type = "boolean"))
@Filter(name = "paidFilter", condition = "paid =:paid")

@FilterDef(name = "dateFilter",
        parameters = {
                @ParamDef(name = "rangeStart", type = "java.time.LocalDateTime"),
                @ParamDef(name = "rangeEnd", type = "java.time.LocalDateTime")})
@Filter(name = "dateFilter", condition = "event_date >= :rangeStart and event_date <= :rangeEnd")

@FilterDef(name = "stateFilter", parameters = @ParamDef(name = "state", type = "string"))
@Filter(name = "stateFilter", condition = "state =:state")

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "lat")
    private float lat;

    @Column(name = "lon")
    private float lon;

    @Transient
    private int views;

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", category=" + category +
                ", title='" + title + '\'' +
                ", annotation='" + annotation + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", initiator=" + initiator +
                ", createdOn=" + createdOn +
                ", publishedOn=" + publishedOn +
                ", state=" + state +
                ", paid=" + paid +
                ", requestModeration=" + requestModeration +
                ", participantLimit=" + participantLimit +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';

    }

}
