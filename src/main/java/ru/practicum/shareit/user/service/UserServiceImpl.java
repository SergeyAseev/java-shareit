package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistsElementException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        if (inMemoryUserStorage.isEmailExists(user.getEmail())) {
            throw new ExistsElementException("User exists");
        }
        validate(user);
        log.info("User with ID {} was created", user.getId());
        User createdUser = inMemoryUserStorage.createUser(user);

        return UserMapper.toUserDto(createdUser);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        User userFromDto = UserMapper.toUser(userDto);
        getUserById(userId);
        User updatedUser = getUserValid(userId, userFromDto);
        log.info("Updated user {}", userFromDto);
        return UserMapper.toUserDto(inMemoryUserStorage.updateUser(userId, updatedUser));
    }

    public void removeUserById(Long userId) {
        getUserById(userId);
        inMemoryUserStorage.removeById(userId);
    }

    public UserDto getUserById(Long userId) {
        User user = inMemoryUserStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> retrieveAllUsers() {
        return inMemoryUserStorage.retrieveAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Проверка создаваемого пользователя на валидность
     *
     * @param user экземпляр текущего пользователя
     */
    private void validate(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Email not found");
        }
        if (user.getName().isEmpty()) {
            throw new ValidationException("Name have to be not empty");
        }
    }

    private User getUserValid(long userId, User user) {
        User updatedUser = UserMapper.toUser(getUserById(userId));

        String updatedName = user.getName();
        if (updatedName != null && !updatedName.isBlank())
            updatedUser.setName(updatedName);

        String updatedEmail = user.getEmail();
        if (updatedEmail != null && !updatedEmail.isBlank()) {

            if (inMemoryUserStorage.getAllEmails().contains(updatedEmail)) {
                throw new ExistsElementException("User exists");
            }

            String oldEmail = updatedUser.getEmail();
            inMemoryUserStorage.getAllEmails().remove(oldEmail);
            updatedUser.setEmail(updatedEmail);
        }
        return updatedUser;
    }
}
