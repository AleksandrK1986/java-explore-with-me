package ru.practicum.service;

import ru.practicum.model.category.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findCategories(int from, int size);

    Category create(Category data);

    Category update(Category data);

    Category findById(long id);

    void delete(long id);
}
