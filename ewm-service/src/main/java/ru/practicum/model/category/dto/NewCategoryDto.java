package ru.practicum.model.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NewCategoryDto {
    @NotBlank
    private String name;

    @Override
    public String toString() {
        return "CategoryDto{" +
                "name='" + name + '\'' +
                '}';
    }
}
