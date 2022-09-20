package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.InMemoryItemStorage;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private InMemoryItemStorage inMemoryItemStorage;

    @Autowired
    private UserService userService;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = UserMapper.toUser(userService.getUserById(userId));
        Item item = ItemMapper.toItem(itemDto, owner);

        validate(item);
        item.setOwner(owner);
        log.info("Create Item with ID {}", item.getId());
        return ItemMapper.toItemDto(inMemoryItemStorage.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        User owner = UserMapper.toUser(userService.getUserById(userId));
        Item item = ItemMapper.toItem(itemDto, owner);

        Item updatedItem = getItemValid(item, itemId, userId);
        log.info("Updated item with ID {}", itemId);
        return ItemMapper.toItemDto(inMemoryItemStorage.updateItem(itemId, updatedItem));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = inMemoryItemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID %s not found", itemId)));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Item getItemByIdWithoutDto(Long itemId) {
        return inMemoryItemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID %s not found", itemId)));
    }

    @Override
    public List<ItemDto> retrieveAllItemByUserId(Long userId) {
        if (userId == null) {
            return inMemoryItemStorage.retrieveAllItems()
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return inMemoryItemStorage.retrieveAllItemsByUser(userId)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<ItemDto> searchItemByKeyword(String keyword) {
        if (keyword.isBlank() || keyword.isEmpty()) {
            return new ArrayList<>();
        }
        return inMemoryItemStorage.searchItemByKeyword(keyword)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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
        Item updatedItem = getItemByIdWithoutDto(itemId);
        User user = UserMapper.toUser(userService.getUserById(userId));

        if (user != null && !updatedItem.getOwner().getId().equals(userId))
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
