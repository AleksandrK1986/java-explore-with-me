package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.user.User;

import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findEventsByInitiatorOrderById(User user, Pageable page);

    List<Event> findByCategoryId(Long categoryId);

    @Query("SELECT ev FROM Event AS ev " +
            "WHERE (LOWER(ev.annotation) LIKE CONCAT('%',LOWER(:text),'%') OR " +
            "LOWER(ev.description) LIKE CONCAT('%',LOWER(:text),'%'))")
    List<Event> findByText(String text);

    @Query("SELECT ev FROM Event AS ev " +
            "WHERE (ev.category.id IN :categories) AND " +
            "(LOWER(ev.annotation) LIKE CONCAT('%',LOWER(:text),'%') OR " +
            "LOWER(ev.description) LIKE CONCAT('%',LOWER(:text),'%'))")
    List<Event> findByCategoryIdsAndText(Collection<Long> categories, String text);

    @Query("SELECT ev FROM Event AS ev " +
            "WHERE (ev.category.id IN :categories) AND " +
            "(ev.initiator.id IN :users) AND " +
            "(ev.state IN :states)")
    List<Event> findByUsersAndCategoriesAndStates(Collection<Long> users, Collection<Long> categories,
                                                  Collection<EventState> states, Pageable pageable);

    @Query("SELECT ev FROM Event AS ev " +
            "WHERE (ev.state IN :states)")
    List<Event> findByStates(Collection<EventState> states, Pageable pageable);

    @Query("SELECT ev FROM Event AS ev " +
            "WHERE (ev.category.id IN :categories) AND " +
            "(ev.state IN :states)")
    List<Event> findByCategoriesAndStates(Collection<Long> categories, Collection<EventState> states, Pageable pageable);

    @Query("SELECT ev FROM Event AS ev " +
            "WHERE (ev.initiator.id IN :users) AND " +
            "(ev.state IN :states)")
    List<Event> findByUsersAndStates(Collection<Long> users, Collection<EventState> states, Pageable pageable);
}
