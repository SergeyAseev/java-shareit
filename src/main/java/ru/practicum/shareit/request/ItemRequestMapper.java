package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        List<ItemRequestDto.Item> itemRequestDtos = null;
        if (items != null) {
            itemRequestDtos = items
                    .stream()
                    .map(item -> ItemRequestDto.Item.builder()
                            .id(item.getId())
                            .available(item.getAvailable())
                            .requestId(item.getRequest().getId())
                            .description(item.getDescription())
                            .name(item.getName())
                            .build())
                    .collect(Collectors.toList());
        }

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequestDtos)
                .build();

    }
}
