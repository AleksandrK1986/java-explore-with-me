package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.web.bind.annotation.*;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.UserDto;
import ru.practicum.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.model.user.dto.UserMapper.toUser;
import static ru.practicum.model.user.dto.UserMapper.toUserDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/users")
public class AdminUserController {
    private UserService service;

    @Autowired
    public AdminUserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        log.info("Get admin/users with userId={}", id);
        return toUserDto(service.findById(id));
    }

    @GetMapping
    public List<UserDto> findUsers(@RequestParam(name = "ids") long[] ids,
                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                   @Positive @RequestParam(name = "size", defaultValue = "100") Integer size) {
        List<User> users = service.findUsers(ids, from, size);
        List<UserDto> usersDto = new ArrayList<>();
        for (User u : users) {
            usersDto.add(toUserDto(u));
        }
        log.info("Get all users with ids={}, from={}, size={}", ids, from, size);
        return usersDto;
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Create user with body {}", userDto);
        return toUserDto(service.create(toUser(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Update user with userId={}, body: {}", userId, userDto);
        return toUserDto(service.update(toUser(userDto), userId));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Delete user with userId={}", id);
        service.delete(id);
    }
}
