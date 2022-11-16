package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.dto.CommentDto;
import ru.practicum.model.comment.dto.NewCommentDto;
import ru.practicum.model.comment.dto.UpdateCommentDto;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.model.comment.dto.CommentMapper.toComment;
import static ru.practicum.model.comment.dto.CommentMapper.toCommentDto;
import static ru.practicum.model.event.dto.EventMapper.toEventShortDto;
import static ru.practicum.model.user.dto.UserMapper.toUserDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users/{userId}/comments")
public class PrivateCommentController {

    private CommentService commentService;
    private UserService userService;
    private EventService eventService;

    @Autowired
    public PrivateCommentController(CommentService commentService,
                                    UserService userService,
                                    EventService eventService) {
        this.commentService = commentService;
        this.userService = userService;
        this.eventService = eventService;
    }

    @PostMapping
    public CommentDto create(@RequestBody @Valid NewCommentDto newCommentDto,
                             @PathVariable long userId) {
        log.info("Controller: create comment by userI={}, comment={}", userId, newCommentDto);
        Comment comment = toComment(newCommentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setUser(userService.findById(userId));
        comment.setEvent(eventService.findById(newCommentDto.getEventId()));
        comment = commentService.create(userId, comment);
        CommentDto commentDto = toCommentDto(comment);
        commentDto.setUser(toUserDto(comment.getUser()));
        commentDto.setEvent(toEventShortDto(comment.getEvent()));
        log.info("Controller: return creating comment by userI={}, comment={}", userId, commentDto);
        return commentDto;
    }

    @PutMapping
    public CommentDto update(@RequestBody @Valid UpdateCommentDto updateCommentDto,
                             @PathVariable long userId) {
        log.info("Controller: update comment by userI={}, comment={}", userId, updateCommentDto);
        Comment comment = toComment(updateCommentDto);
        comment = commentService.update(userId, comment);
        CommentDto commentDto = toCommentDto(comment);
        commentDto.setUser(toUserDto(comment.getUser()));
        commentDto.setEvent(toEventShortDto(comment.getEvent()));
        log.info("Controller: return updating comment by userI={}, comment={}", userId, commentDto);
        return commentDto;
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable Long userId,
                       @PathVariable Long commentId) {
        log.info("Controller: delete comment by userI={}, commentId={}", userId, commentId);
        commentService.delete(userId, commentId);
    }


    @GetMapping
    public List<CommentDto> findCommentsByText(@PathVariable long userId,
                                               @RequestParam(name = "text", defaultValue = "") String text,
                                               @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        List<Comment> comments = commentService.findCommentsByText(userId, text, from, size);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment c : comments) {
            CommentDto commentDto = toCommentDto(c);
            commentDto.setUser(toUserDto(c.getUser()));
            commentDto.setEvent(toEventShortDto(c.getEvent()));
            commentDtos.add(commentDto);
        }
        log.info("Controller: find comments by text={}: {}", text, commentDtos);
        return commentDtos;
    }

}
