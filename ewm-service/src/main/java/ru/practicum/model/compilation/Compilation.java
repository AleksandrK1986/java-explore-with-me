package ru.practicum.model.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.event.Event;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 1, max = 255)
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "pinned", nullable = false)
    private Boolean pinned;

    @ManyToMany
    @JoinTable(
            name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;

    public void addEvent(Event event) {
        events.add(event);
    }

    public void deleteEvent(long eventId) {
        Event deleteEvent = new Event();
        for (Event e : events) {
            if (e.getId() == eventId) {
                deleteEvent = e;
            }
        }
        events.remove(deleteEvent);
    }

    @Override
    public String toString() {
        return "Compilation{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", pinned=" + pinned +
                ", events=" + events +
                '}';
    }
}
