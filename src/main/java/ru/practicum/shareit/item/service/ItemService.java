package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

   Item createItem(Item item, Long user);

   Item updateItem(Item item, Long itemId, Long userId);

   Item getItemById(Long itemId);

   List<Item> retrieveAllItemByUserId(Long userId);

   List<Item> searchItemByKeyword(String keyword);
}
