package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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
     * @param userId  ID владельца предмета
     * @return экземпляр обновленного предмета
     */
    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    /**
     * Метод получения предмета по ID
     *
     * @param itemId ID предмета
     * @return экзепляр сущности предмета
     */
    ItemDto getItemById(Long itemId);

    /**
     * Метод получения предметов пользователя
     *
     * @param userId ID владельца предемета
     * @return список экземпляров предметов-дто пользователя
     */
    List<ItemDto> retrieveAllItemByUserId(Long userId);

    /**
     * Метод поиска предмета по ключевому слову
     *
     * @param keyword ключевое слово
     * @return список экземпляров предметов-дто, которые содержат ключевое слово
     */
    List<ItemDto> searchItemByKeyword(String keyword);

    Item getItemByIdWithoutDto(Long itemId);
}
