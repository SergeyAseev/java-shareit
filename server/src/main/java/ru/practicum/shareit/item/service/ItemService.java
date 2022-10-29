package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {

    /**
     * Метод создания премета
     *
     * @param itemDto экземпляр сущности предмета-дто, которые создается
     * @param user    экземпляр сущности владельца предмета
     * @return экземпляр созданного предмета
     */
    ItemDto createItem(ItemDto itemDto, Long user);

    /**
     * Метод обновления существующего предмета
     *
     * @param itemDto экземпляр сущности предмета-дто, который обновляется
     * @param itemId  ID предмета
     * @param ownerId ID владельца предмета
     * @return экземпляр обновленного предмета
     */
    ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    /**
     * Метод получения предмета по ID
     *
     * @param itemId ID предмета
     * @param userId ID пользователя
     * @return экзепляр сущности предмета
     */
    ItemDtoWithBooking getItemById(Long itemId, Long userId);

    /**
     * Метод получения предметов пользователя
     *
     * @param ownerId ID владельца предемета
     * @return список экземпляров предметов-дто пользователя
     */
    List<ItemDtoWithBooking> retrieveAllItemByUserId(Long ownerId);

    /**
     * Метод поиска предмета по ключевому слову
     *
     * @param keyword ключевое слово
     * @return список экземпляров предметов-дто, которые содержат ключевое слово
     */
    List<ItemDto> searchItemByKeyword(String keyword);

    /**
     * Метод добавления отзыва
     *
     * @param itemId     ID предмета
     * @param userId     ID пользователя, кто оставляет запись
     * @param commentDto экземляр сущности commentDto
     * @return экземляр сущности созданного отзыва
     */
    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
