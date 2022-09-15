package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistsElementException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final InMemoryUserStorage inMemoryUserStorage;

    public User createUser(User user) {

        if (inMemoryUserStorage.getAllEmails().contains(user.getEmail())) {
            throw new ExistsElementException("User exists");
        }
        validate(user);
        log.info("User with ID {} was created", user.getId());
        return inMemoryUserStorage.createUser(user);
    }

    public User updateUser(Long userId, User user) {
        User updatedUser = getValidUser(userId, user);
        log.info("Updated user {}", user);
        return inMemoryUserStorage.updateUser(userId, updatedUser);
    }

    public User removeUserById(Long userId) {
        return inMemoryUserStorage.removeById(userId);
    }

    public User getUserById(Long userId) {
        return inMemoryUserStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден пользователь с ID %s", userId)));
    }

    public List<User> retrieveAllUsers() {
        return new ArrayList<>(inMemoryUserStorage.retrieveAllUsers());
    }

    /**
     * Проверка создаваемого пользователя на валидность
     * @param user экземпляр текущего пользователя
     */
    private void validate(User user) {
        if (user.getEmail() ==null) {
            throw new ValidationException("Email not found");
        }
        if (user.getName().isEmpty()) {
            throw new ValidationException("Имя не может быть пустым");
        }
    }

    private User getValidUser(long userId, User user) {
        User updatedUser = inMemoryUserStorage.getUserById(userId).orElseThrow(
                () -> new NoSuchElementException("User not found"));

        String updatedName = user.getName();
        if (updatedName != null && !updatedName.isBlank())
            updatedUser.setName(updatedName);

        String oldEmail = updatedUser.getEmail();
        inMemoryUserStorage.getAllEmails().remove(oldEmail);

        String updatedEmail = user.getEmail();
        if (updatedEmail != null && !updatedEmail.isBlank()) {

            if (inMemoryUserStorage.getAllEmails().contains(updatedEmail)) {
                throw new ExistsElementException("User exists");
            }
            updatedUser.setEmail(updatedEmail);
        }
        return updatedUser;
    }
}
