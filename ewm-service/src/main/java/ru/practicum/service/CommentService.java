package ru.practicum.service;

import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentStatus;

import java.util.List;

public interface CommentService {

    List<Comment> findCommentsByUser(long userId, int from, int size);
    List<Comment> findCommentsByEvent(long eventId, int from, int size);
    Comment findCommentById(long commentId);

    Comment create(long userId, Comment comment);
    Comment update(long userId, Comment updateComment);
    void delete(long userId, long commentId);
    List<Comment> findCommentsByText(long userId, String text, int from, int size);
    Comment adminApprove(long commentId);
    Comment adminReject(long commentId);
    List<Comment> adminFindComments(long eventId, CommentStatus status, int from, int size);
    void adminDelete(long commentId);

}
