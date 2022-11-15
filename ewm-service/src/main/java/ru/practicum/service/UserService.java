package ru.practicum.service;

import ru.practicum.model.user.User;

import java.util.List;

public interface UserService {
    List<User> findUsers(int[] ids, int from, int size);

    User create(User data);

    User update(User data, long userId);

    User findById(long id);

    void delete(long id);
}
