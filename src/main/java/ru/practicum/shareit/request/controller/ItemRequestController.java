package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    @Autowired
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping()
    public List<ItemRequestDto> getAllMyItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllMyItemRequest(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long id) {
        return itemRequestService.getItemRequestById(userId, id);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(required = false, defaultValue = "0") Integer from,
                                                      @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemRequestService.findAll(userId, from, size);
    }
}
