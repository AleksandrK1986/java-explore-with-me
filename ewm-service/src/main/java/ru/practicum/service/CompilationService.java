package ru.practicum.service;

import ru.practicum.model.compilation.Compilation;

import java.util.List;

public interface CompilationService {
    Compilation create(Compilation compilation);

    void delete(long id);

    void deleteEvent(long compId, long eventId);

    void unPin(long compId);

    void pin(long compId);

    void addEvent(long compId, long eventId);

    List<Compilation> findAll(boolean pinned, int from, int size);

    Compilation findById(long id);

}
