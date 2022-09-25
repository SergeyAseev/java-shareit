package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    protected long itemId = 0L;

    public long increaseItemId() {
        return ++itemId;
    }

    public Item save(Item item) {
        item.setId(increaseItemId());
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Long itemId, Item item) {
        items.put(itemId, item);
        return item;
    }

    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public List<Item> searchItemByKeyword(String keyword) {

        String toLowerCase = keyword.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> (item.getName().toLowerCase().contains(toLowerCase)
                        || item.getDescription().toLowerCase().contains(toLowerCase)))
                .collect(Collectors.toList());
    }

    public List<Item> retrieveAllItems() {
        return new ArrayList<>(items.values());
    }

    public List<Item> retrieveAllItemsByUser(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId)).collect(Collectors.toList());
    }
}


