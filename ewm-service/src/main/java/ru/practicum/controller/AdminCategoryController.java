package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.model.category.dto.NewCategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.Valid;

import static ru.practicum.model.category.dto.CategoryMapper.toCategory;
import static ru.practicum.model.category.dto.CategoryMapper.toCategoryDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private CategoryService service;

    @Autowired
    public AdminCategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public CategoryDto create(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.info("Controller: create category {}", categoryDto);
        CategoryDto returnCategory = toCategoryDto(service.create(toCategory(categoryDto)));
        log.info("Controller: return category {}", returnCategory);
        return returnCategory;
    }

    @PatchMapping
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Controller: update category {}", categoryDto);
        CategoryDto returnCategory = toCategoryDto(service.update(toCategory(categoryDto)));
        log.info("Controller: return updated category {}", returnCategory);
        return returnCategory;
    }

    @DeleteMapping("/{catId}")
    public void delete(@PathVariable long catId) {
        log.info("Controller: delete category with id {}", catId);
        service.delete(catId);
    }

}
