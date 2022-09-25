package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage {

    protected long userId = 0L;

    private final Map<Long, User> users = new HashMap<>();

    private final Set<String> emails = new HashSet<>();

    public long increaseUserId() {
        return ++userId;
    }

    public User createUser(User user) {
        user.setId(increaseUserId());
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(Long userId, User user) {
        users.put(userId, user);
        return user;
    }

    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Set<String> getAllEmails() {
        return emails;
    }

    public List<User> retrieveAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void removeById(long userId) {
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    public boolean isEmailExists(String email) {
        return emails.contains(email);
    }

}
