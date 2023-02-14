package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    /**
     * Метод создания запроса
     *
     * @param itemRequestDto экземляр сущности запроса-дто, который создается
     * @return созданный экземляр сущности запроса
     */
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    /**
     * Метод получения всех своих запросов
     *
     * @param userId ID того, кто запрашивает
     * @return отсортированный по возрастанию список собственных запросов
     */
    List<ItemRequestDto> getAllMyItemRequest(Long userId);

    /**
     * Метод получения запроса вещи по идентификатору запроса
     *
     * @param itemRequestId ID запроса
     * @return экземляр сущности-запроса
     */
    ItemRequestDto getItemRequestById(Long userId, Long itemRequestId);

    /**
     * Постраничный поиск
     *
     * @param userId ID пользователя, чья запросы запрашиваем
     * @param from   для пагинации
     * @param size   для пагинации
     * @return отсортированный по убыванию список запросов
     */
    List<ItemRequestDto> findAll(Long userId, int from, int size);
}
