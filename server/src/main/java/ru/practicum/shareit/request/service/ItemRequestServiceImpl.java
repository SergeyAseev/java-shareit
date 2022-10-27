package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        validate(itemRequest);
        itemRequest.setCreated(LocalDateTime.now());

        log.info("Create ItemRequest with ID {}", itemRequestDto.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }

    /**
     * Долг с прошлого раза с предложением написать тесты.
     * Посмотрев код еще раз понял, что замечения верно и метод работает неправильно, так что
     * тесты писать не стал. Начав переписывать пришел к первончальному варианту с небольшим рефакторингом
     * в виде вынесения дублирующегося кода в отдельный метод.
     */
    @Override
    public List<ItemRequestDto> getAllMyItemRequest(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequester_IdOrderByCreatedAsc(userId);
        return retrieveRequestsForItems(itemRequests);
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId, int from, int size) {

        if (size <= 0 || from < 0) {
            throw new ValidationException("size and from have to positive");
        }
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequester_IdNot(userId,
                PageRequest.of(from / size, size, Sort.by("created").descending()));
        return retrieveRequestsForItems(itemRequests);
    }

    public List<ItemRequestDto> retrieveRequestsForItems(List<ItemRequest> itemRequests) {
        return itemRequests
                .stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.findByRequest_Id(itemRequest.getId(), Sort.by("id").descending());
                    return ItemRequestMapper.toItemRequestDto(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long itemRequestId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format("ItemRequest with ID %s not found", itemRequestId)));
        List<Item> items = itemRepository.findByRequest_Id(itemRequest.getId(), Sort.by("id").descending());

        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    private void validate(ItemRequest itemRequest) {

        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new ValidationException("Description has to be not empty");
        }
    }
}
