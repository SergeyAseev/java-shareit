package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserService {

    /**
     * Метод создания пользователя
     *
     * @param userDto экземпляр сущности пользователя-дто
     * @return экземпляр созданного пользователя-дто
     */
    UserDto createUser(UserDto userDto);

    /**
     * Метод обновления пользователя
     *
     * @param userId  ID пользователя
     * @param userDto экземпляр сущности пользователя-дто
     * @return обновленный экземпляр сущности пользователя-дто
     */
    UserDto updateUser(Long userId, UserDto userDto);

    /**
     * Метод удаления пользователя по ID
     *
     * @param userId ID пользователя
     */
    void removeUserById(Long userId);

    /**
     * Метод получения пользователя по ID
     *
     * @param userId ID пользователя
     * @return экземпляр сущности пользователя-дто
     */
    UserDto getUserById(Long userId);

    /**
     * Метод возврата всех пользователей
     *
     * @return список экземляров пользователей-дто
     */
    List<UserDto> retrieveAllUsers();
}
