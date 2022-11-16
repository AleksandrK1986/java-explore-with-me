package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.errors.exception.ForbiddenException;
import ru.practicum.errors.exception.NotValidException;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentStatus;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;


@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Comment> findCommentsByUser(long userId, int from, int size) {
        checkAndGetUser(userId);
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page;
        if (from != 0) {
            page = PageRequest.of(from / size, from / size, sortBy);
        } else {
            page = PageRequest.of(0, size, sortBy);
        }
        List<Comment> comments = commentRepository.findByUserId(userId, page);
        log.info("Service: find comments by userId={}: {}", userId, comments);
        return comments;
    }

    @Override
    public List<Comment> findCommentsByEvent(long eventId, int from, int size) {
        checkAndGetEvent(eventId);
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page;
        if (from != 0) {
            page = PageRequest.of(from / size, from / size, sortBy);
        } else {
            page = PageRequest.of(0, size, sortBy);
        }
        List<Comment> comments = commentRepository.findByEventId(eventId, page);
        log.info("Service: find comments by userId={}: {}", eventId, comments);
        return comments;
    }

    @Override
    public Comment findCommentById(long commentId) {
        Comment comment = checkAndGetComment(commentId);
        log.info("Service: find comments by commentId={}: {}", commentId, comment);
        return comment;
    }

    @Override
    public Comment create(long userId, Comment comment) {
        checkAndGetUser(userId);
        log.info("Service: create comment by userId={}", userId);
        return commentRepository.save(comment);
    }

    @Override
    public Comment update(long userId, Comment comment) {
        Comment newComment = checkAndGetComment(comment.getId());
        User user = checkAndGetUser(userId);
        checkUserAndComment(userId, comment.getId());
        newComment.setText(comment.getText());
        newComment.setStatus(CommentStatus.PENDING);
        log.info("Service: update comment by userId={}: {}", userId, newComment);
        return commentRepository.save(newComment);
    }

    @Override
    public void delete(long userId, long commentId) {
        checkAndGetUser(userId);
        checkAndGetComment(commentId);
        checkUserAndComment(userId, commentId);
        log.info("Service: delete comment by userId={}, commentId={}", userId, commentId);
        commentRepository.deleteById(commentId);
    }

    private User checkAndGetUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ForbiddenException("Пользователь не найден в хранилище");
        }
        return userRepository.getReferenceById(userId);
    }

    @Override
    public List<Comment> findCommentsByText(long userId, String text, int from, int size) {
        checkAndGetUser(userId);
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page;
        if (from != 0) {
            page = PageRequest.of(from / size, from / size, sortBy);
        } else {
            page = PageRequest.of(0, size, sortBy);
        }
        List<Comment> comments = commentRepository.findCommentsByTextContainingIgnoreCase(text, page);
        log.info("Service: find comments by text={}: {}", text, comments);
        return comments;
    }

    @Override
    public List<Comment> adminFindComments(long eventId, CommentStatus status, int from, int size) {
        checkAndGetEvent(eventId);
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page;
        if (from != 0) {
            page = PageRequest.of(from / size, from / size, sortBy);
        } else {
            page = PageRequest.of(0, size, sortBy);
        }
        List<Comment> comments = commentRepository.findByEventIdAndStatus(eventId, status, page);
        log.info("Service: find admin comments by eventId={} и status={}: {}", eventId, status, comments);
        return comments;
    }

    @Override
    public Comment adminApprove(long commentId) {
        Comment comment = checkAndGetComment(commentId);
        if (comment.getStatus() == CommentStatus.APPROVED) {
            throw new ForbiddenException("Комментарий не может быть подтвержден дважды");
        }
        comment.setStatus(CommentStatus.APPROVED);
        log.info("Service: approve comment by commentId={}: {}", commentId, comment);
        return commentRepository.save(comment);
    }

    @Override
    public Comment adminReject(long commentId) {
        Comment comment = checkAndGetComment(commentId);
        if (comment.getStatus() == CommentStatus.REJECTED) {
            throw new ForbiddenException("Комментарий не может быть отменен дважды");
        }
        comment.setStatus(CommentStatus.REJECTED);
        log.info("Service: reject comment by commentId={}: {}", commentId, comment);
        return commentRepository.save(comment);
    }

    @Override
    public void adminDelete(long commentId) {
        checkAndGetComment(commentId);
        log.info("Service: delete comment by commentId={}", commentId);
        commentRepository.deleteById(commentId);
    }

    private Event checkAndGetEvent(long eventId) {
        if (eventRepository.existsById(eventId)) {
            throw new NotValidException("Событие не найдено в хранилище");
        }
        return eventRepository.getReferenceById(eventId);
    }

    private Comment checkAndGetComment(long commentId) {
        if (commentRepository.existsById(commentId)) {
            throw new NotValidException("Комментарий не найдено в хранилище");
        }
        return commentRepository.getReferenceById(commentId);
    }

    private void checkUserAndComment(long userId, long commentId) {
        if (commentRepository.getReferenceById(commentId).getUser() != userRepository.getReferenceById(userId)) {
            throw new ForbiddenException("Пользователь не является автором комментария");
        }
    }
}
