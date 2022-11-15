package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.errors.exception.AlreadyExistException;
import ru.practicum.model.user.User;
import ru.practicum.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        checkUserEmail(user);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AlreadyExistException("Пользователь с электронной почтой существует");
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> findUsers(int[] ids, int from, int size) {
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page;
        if (from != 0) {
            page = PageRequest.of(from / size, from / size, sortBy);
        } else {
            page = PageRequest.of(0, size, sortBy);
        }
        Page<User> users = userRepository.findAll(page);

        if (ids[0] == 0) {
            return users.getContent();
        } else {
            List<User> userFromIds = new ArrayList<>();
            for (int i : ids) {
                for (User u : users) {
                    if (u.getId() == i) {
                        userFromIds.add(u);
                    }
                }
            }
            return userFromIds;
        }
    }

    @Override
    public User update(User user, long userId) {
        checkUser(userId);
        User newUser = userRepository.getReferenceById(userId);
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        return userRepository.save(newUser);
    }

    @Override
    public User findById(long id) {
        checkUser(id);
        return userRepository.getReferenceById(id);
    }

    @Override
    public void delete(long id) {
        checkUser(id);
        userRepository.delete(userRepository.getReferenceById(id));
    }

    private void checkUserEmail(User user) {
        if (user.getEmail() == null) {
            throw new NullPointerException("Передан пустой email");
        }
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new NullPointerException("Передан некорректный email");
        }
    }

    private void checkUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchElementException("Пользователь не найден в хранилище");
        }
    }
}
