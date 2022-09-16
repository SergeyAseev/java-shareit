package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.InMemoryItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    InMemoryItemStorage inMemoryItemStorage;
    @Autowired
    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    UserService userService;

    @Override
    public Item createItem(Item item, Long userId) {
        validate(item);
        item.setOwner(userService.getUserById(userId));
        log.info("Create Item with ID {}", item.getId());
        return inMemoryItemStorage.save(item);
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        Item updatedItem = getItemValid(item, itemId, userId);
        log.info("Updated item with ID {}", itemId);
        return inMemoryItemStorage.updateItem(itemId, updatedItem);
    }

    @Override
    public Item getItemById(Long itemId) {
        return inMemoryItemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID %s not found", itemId)));
    }

    @Override
    public List<Item> retrieveAllItemByUserId(Long userId) {
        if (userId == null) {
            return inMemoryItemStorage.retrieveAllItems();
        } else {
            return inMemoryItemStorage.retrieveAllItemsByUser(userId);
        }
    }

    @Override
    public List<Item> searchItemByKeyword(String keyword) {
        if (keyword.isBlank() || keyword.isEmpty()) {
            return new ArrayList<>();
        }
        return inMemoryItemStorage.searchItemByKeyword(keyword);
    }

    private void validate(Item item) {
        if (item.getDescription() == null) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (item.getName().isBlank()) {
            throw new ValidationException("Название предмета не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Статус доступа не может быть пустым");
        }

    }

    private Item getItemValid(Item item, Long itemId, Long userId) {
        Item updatedItem = getItemById(itemId);
        if (inMemoryUserStorage.getUserById(userId).isPresent() && !updatedItem.getOwner().getId().equals(userId))
            throw new NotFoundException("Предмет не доступен для брони");

        String updatedDescription = item.getDescription();
        if (updatedDescription != null && !updatedDescription.isBlank()) {
            updatedItem.setDescription(updatedDescription);
        }
        String updatedName = item.getName();
        if (updatedName != null && !updatedName.isBlank()) {
            updatedItem.setName(updatedName);
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return updatedItem;
    }
}
