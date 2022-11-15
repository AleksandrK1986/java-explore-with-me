package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.errors.exception.AlreadyExistException;
import ru.practicum.errors.exception.NotFoundException;
import ru.practicum.errors.exception.NotValidException;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;
    private EventRepository eventRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Category create(Category category) {
        try {
            Category categoryFromDb = categoryRepository.save(category);
            log.info("Service: return category from Db {}", categoryFromDb);
            return categoryFromDb;
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException("Категория с названием уже существует");
        }
    }

    @Override
    public Category update(Category category) {
        Category categoryFromDb = categoryRepository.getReferenceById(category.getId());
        if (category.getName() == null) {
            throw new NotValidException("Передано пустое имя");
        }
        categoryFromDb.setName(category.getName());
        log.info("Service: update category {}", categoryFromDb);
        return categoryRepository.save(categoryFromDb);
    }

    @Override
    public Category findById(long id) {
        checkCategory(id);
        log.info("Service: find category by id {}", id);
        return categoryRepository.getReferenceById(id);
    }

    @Override
    public void delete(long id) {
        checkCategory(id);
        List<Event> events = eventRepository.findByCategoryId(id);
        if (!events.isEmpty()) {
            throw new NotValidException("Нельзя удалить категорию с событиями!");
        }
        log.info("Service: delete category {}", categoryRepository.getReferenceById(id));
        categoryRepository.delete(categoryRepository.getReferenceById(id));
    }

    @Override
    public List<Category> findCategories(int from, int size) {
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page;
        if (from != 0) {
            page = PageRequest.of(from / size, from / size, sortBy);
        } else {
            page = PageRequest.of(0, size, sortBy);
        }
        Page<Category> categories = categoryRepository.findAll(page);
        log.info("Service: findCategories {}", categories.getContent());
        return categories.getContent();
    }

    private void checkCategory(long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Не существует категории с id " + id);
        }
    }

}
