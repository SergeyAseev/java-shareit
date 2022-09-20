package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto, @PathVariable Long id) {
        return itemService.updateItem(itemDto, id, userId);
    }

    @GetMapping(value = "/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> retrieveAllItem(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.retrieveAllItemByUserId(userId);
    }


    @GetMapping("/search")
    public List<ItemDto> searchItemByKeyword(@RequestParam(name = "text", defaultValue = "") String keyword) {
        return itemService.searchItemByKeyword(keyword);
    }
}
