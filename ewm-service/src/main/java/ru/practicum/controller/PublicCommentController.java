package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.dto.CommentDto;
import ru.practicum.service.CommentService;

import javax.validation.Valid;
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
@RequestMapping(path = "/comments")
public class PublicCommentController {

    private CommentService commentService;

    @Autowired
    public PublicCommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping("/user/{userId}")
    public List<CommentDto> findCommentsByUser(@PathVariable long userId,
                                               @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        List<Comment> comments = commentService.findCommentsByUser(userId, from, size);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment c : comments) {
            CommentDto commentDto = toCommentDto(c);
            commentDto.setUser(toUserDto(c.getUser()));
            commentDto.setEvent(toEventShortDto(c.getEvent()));
            commentDtos.add(commentDto);
        }
        log.info("Controller: find comments by userI={}", userId);
        return commentDtos;
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> findCommentsByEvent(@PathVariable long eventId,
                                                @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        List<Comment> comments = commentService.findCommentsByEvent(eventId, from, size);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment c : comments) {
            CommentDto commentDto = toCommentDto(c);
            commentDto.setUser(toUserDto(c.getUser()));
            commentDto.setEvent(toEventShortDto(c.getEvent()));
            commentDtos.add(commentDto);
        }
        log.info("Controller: find comments by eventId={}", eventId);
        return commentDtos;
    }

    @GetMapping("/{commentId}")
    public CommentDto findCommentById(@PathVariable long commentId) {
        log.info("Controller: find comment by commentId={}", commentId);
        Comment comment = commentService.findCommentById(commentId);
        CommentDto commentDto = toCommentDto(comment);
        commentDto.setUser(toUserDto(comment.getUser()));
        commentDto.setEvent(toEventShortDto(comment.getEvent()));
        log.info("Controller: return comment by commentId={}: {}", commentId, commentDto);
        return commentDto;
    }


}
