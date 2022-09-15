package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(Long userId, User user);

    User removeUserById(Long userId);

    User getUserById(Long userId);

    List<User> retrieveAllUsers();
}
