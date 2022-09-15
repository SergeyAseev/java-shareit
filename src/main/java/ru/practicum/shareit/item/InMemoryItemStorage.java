package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Component
public class InMemoryItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    protected long itemId = 0L;

    public long increaseItemId() {
        return ++itemId;
    }

    public Item save(Item item){
        item.setId(increaseItemId());
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public void removeItemById(Long itemId) {
        Item item = getItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден предмет с ID %s", itemId)));
        items.remove(itemId);
    }
    public List<Item> retrieveAllUsers() {
        return new ArrayList<>(items.values());
    }
}


