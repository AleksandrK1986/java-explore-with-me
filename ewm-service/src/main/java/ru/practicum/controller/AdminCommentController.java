package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentStatus;
import ru.practicum.model.comment.dto.CommentDto;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;
import ru.practicum.service.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.model.comment.dto.CommentMapper.toCommentDto;
import static ru.practicum.model.event.dto.EventMapper.toEventShortDto;
import static ru.practicum.model.user.dto.UserMapper.toUserDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {

    private CommentService commentService;
    private UserService userService;
    private EventService eventService;

    @Autowired
    public AdminCommentController(CommentService commentService,
                                  UserService userService,
                                  EventService eventService) {
        this.commentService = commentService;
        this.userService = userService;
        this.eventService = eventService;
    }

    @PatchMapping("/{commentId}/approve")
    public CommentDto approve(@PathVariable long commentId) {
        log.info("Controller: approve comment by commentId={}", commentId);
        Comment comment = commentService.adminApprove(commentId);
        CommentDto commentDto = toCommentDto(comment);
        commentDto.setUser(toUserDto(comment.getUser()));
        commentDto.setEvent(toEventShortDto(comment.getEvent()));
        log.info("Controller: return approved comment {}", commentDto);
        return commentDto;
    }

    @PatchMapping("/{commentId}/reject")
    public CommentDto reject(@PathVariable long commentId) {
        log.info("Controller: reject comment by commentId={}", commentId);
        Comment comment = commentService.adminReject(commentId);
        CommentDto commentDto = toCommentDto(comment);
        commentDto.setUser(toUserDto(comment.getUser()));
        commentDto.setEvent(toEventShortDto(comment.getEvent()));
        log.info("Controller: return rejected comment {}", commentDto);
        return commentDto;
    }

    @DeleteMapping("/{commentId}")
    public void adminDelete(@PathVariable long commentId) {
        log.info("Controller: delete from admin comment by commentId={}", commentId);
        commentService.adminDelete(commentId);
    }

    @GetMapping
    public List<CommentDto> adminFindByEventAndStatus(@RequestParam CommentStatus status,
                                                      @RequestParam long eventId,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                      @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Controller: find admin comments by eventId={} и status={}}", eventId, status);
        List<Comment> comments = commentService.adminFindComments(eventId, status, from, size);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment c : comments) {
            CommentDto commentDto = toCommentDto(c);
            commentDto.setUser(toUserDto(c.getUser()));
            commentDto.setEvent(toEventShortDto(c.getEvent()));
            commentDtos.add(commentDto);
        }
        log.info("Controller: return admin comments by eventId={} и status={}: {}}", eventId, status, commentDtos);
        return commentDtos;
    }

}
