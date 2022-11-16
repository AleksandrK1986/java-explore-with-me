package ru.practicum.model.comment.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentStatus;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserDto;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                new UserDto(),
                comment.getCreated(),
                new EventShortDto(),
                comment.getStatus()
        );
    }

    public static Comment toComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                new User(),
                commentDto.getCreated(),
                new Event(),
                commentDto.getStatus()
        );
    }

    public static Comment toComment(NewCommentDto commentDto) {
        return new Comment(
                0,
                commentDto.getText(),
                new User(),
                LocalDateTime.now(),
                new Event(),
                CommentStatus.PENDING
        );
    }

    public static Comment toComment(UpdateCommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                new User(),
                LocalDateTime.now(),
                new Event(),
                CommentStatus.PENDING
        );
    }
}
