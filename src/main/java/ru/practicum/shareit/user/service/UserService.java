package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    /**
     * Метод создания пользователя
     * @param user экземпляр сущности пользователя
     * @return экземпляр созданного пользоватля
     */
    User createUser(User user);

    /**
     * Метод обновления пользователя
     * @param userId ID пользователя
     * @param user экземпляр сущности пользователя
     * @return обновленный экземпляр сущности пользователя
     */
    User updateUser(Long userId, User user);

    /**
     * Метод удаления пользователя по ID
     * @param userId ID пользователя
     */
     void removeUserById(Long userId);

    /**
     * Метод получения пользователя по ID
     * @param userId ID пользователя
     * @return экземпляр сущности пользователя
     */
    User getUserById(Long userId);

    /**
     * Метод возврата всех пользователей
     * @return список экземляров пользователей
     */
    List<User> retrieveAllUsers();
}
