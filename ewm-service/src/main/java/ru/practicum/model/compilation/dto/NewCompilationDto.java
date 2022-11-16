package ru.practicum.model.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NewCompilationDto {
    List<Integer> events;
    @Size(min = 1, max = 255)
    @NotBlank
    private String title;
    private Boolean pinned;

    @Override
    public String toString() {
        return "NewCompilationDto{" +
                "events=" + events +
                ", title='" + title + '\'' +
                ", pinned=" + pinned +
                '}';
    }
}
