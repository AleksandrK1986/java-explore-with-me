package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.model.category.dto.CategoryMapper.toCategoryDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/categories")
public class PublicCategoryController {
    private CategoryService service;

    @Autowired
    public PublicCategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoryDto> findAll(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category c : service.findCategories(from, size)) {
            categoryDtos.add(toCategoryDto(c));
        }
        log.info("Controller: find all with from={}, siz={}, categories: {}", from, size, categoryDtos);
        return categoryDtos;
    }

    @GetMapping("/{catId}")
    public CategoryDto findById(@PathVariable long catId) {
        CategoryDto categoryDto = toCategoryDto(service.findById(catId));
        log.info("Controller: find by id={} category: {}", catId, categoryDto);
        return categoryDto;
    }

}
