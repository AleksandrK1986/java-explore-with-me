package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private CompilationRepository compilationRepository;
    private EventRepository eventRepository;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository,
                                  EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Compilation create(Compilation compilation) {
        Compilation compilationFromDb = compilationRepository.save(compilation);
        log.info("Service: return compilation from Db {}", compilationFromDb);
        return compilationFromDb;
    }

    @Override
    public void delete(long compId) {
        log.info("Service: delete compilation from Db with id {}", compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public void deleteEvent(long compId, long eventId) {
        log.info("Service: delete event from compilation with compId={}, eventId={}", compId, eventId);
        Compilation compilation = compilationRepository.getReferenceById(compId);
        compilation.deleteEvent(eventId);
        compilationRepository.save(compilation);
    }

    @Override
    public void addEvent(long compId, long eventId) {
        log.info("Service: add event in compilation with compId={}, eventId={}", compId, eventId);
        Compilation compilation = compilationRepository.getReferenceById(compId);
        compilation.addEvent(eventRepository.getReferenceById(eventId));
        compilationRepository.save(compilation);
    }

    @Override
    public Compilation findById(long compId) {
        Compilation compilationFromDb = compilationRepository.getReferenceById(compId);
        log.info("Service: get compilation {}", compilationFromDb);
        return compilationFromDb;
    }

    @Override
    public void unPin(long compId) {
        log.info("Service: unPin compilation with compId={}", compId);
        Compilation compilation = compilationRepository.getReferenceById(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    public void pin(long compId) {
        log.info("Service: Pin compilation with compId={}", compId);
        Compilation compilation = compilationRepository.getReferenceById(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Override
    public List<Compilation> findAll(boolean pinned, int from, int size) {
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page;
        if (from != 0) {
            page = PageRequest.of(from / size, from / size, sortBy);
        } else {
            page = PageRequest.of(0, size, sortBy);
        }
        Page<Compilation> compilations = compilationRepository.findCompilationsByPinnedOrderById(pinned, page);
        log.info("Service: find compilations, with pinned={}, from={}, size={}", pinned, from, size);
        return compilations.getContent();
    }
}
