package ru.practicum.model.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentDto {
    private Long id;
    @Size(min = 1, max = 1000)
    private String text;
}
