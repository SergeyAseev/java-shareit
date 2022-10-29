package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ItemRequestServiceTest {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestService itemRequestService;
    private final User user = new User(1L, "user1", "user1@mail.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "text", user, LocalDateTime.now());

    @Autowired
    public ItemRequestServiceTest(
            ItemRequestRepository itemRequestRepository,
            ItemRequestService itemRequestService,
            UserService userService) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRequestService = itemRequestService;
        userService.createUser(UserMapper.toUserDto(user));
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void addNewRequestTest() {
        ItemRequestDto itemRequest1 = itemRequestService
                .createItemRequest(itemRequest.getRequester().getId(),
                        ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>()));
        assertEquals(ItemRequestMapper.toItemRequestDto(itemRequestRepository
                .findById(itemRequest1.getId()).orElseThrow(), new ArrayList<>()).getId(), itemRequest1.getId());
    }

    @Test
    void getAllByUserIdTest() {
        assertEquals(itemRequestService.findAll(2L, 0, 10).get(0).getId(),
                List.of(ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>())).get(0).getId());
    }

    @Test
    void getRequestByIdTest() {
        assertEquals(itemRequestService.getItemRequestById(itemRequest.getId(),
                user.getId()).getId(), ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>()).getId());
    }

    @Test
    void getAllRequestOrderByCreatedTest() {
        assertEquals(itemRequestService.getAllMyItemRequest(user.getId()).get(0).getId(),
                List.of(ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>())).get(0).getId());
    }
}