package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.InMemoryItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService{

    @Autowired
    InMemoryItemStorage inMemoryItemStorage;
    @Autowired
    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    UserService userService;

    @Override
    public Item createItem(Item item, Long userId) {
        User owner = userService.getUserById(userId);
        item.setOwner(owner);
        log.info("Create Item with ID {}", item.getId());
        return inMemoryItemStorage.save(item);
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        return null;
    }

    @Override
    public Item getItemById(Long itemId) {
        return inMemoryItemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден предмет с ID %s", itemId)));
    }

    @Override
    public List<Item> retrieveAllItemByUserId(Long userId) {
        return null;
    }

    @Override
    public List<Item> searchItemByKeyword(String keyword) {
        return null;
    }
}
