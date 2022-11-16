package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentStatus;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUserId(long userId, Pageable page);

    List<Comment> findByEventId(long userId, Pageable page);

    List<Comment> findByEventIdAndStatus(long eventId, CommentStatus status, Pageable page);

    @Query("FROM Comment WHERE text like :text")
    List<Comment> findCommentsByTextContainingIgnoreCase(String text, Pageable page);
}
