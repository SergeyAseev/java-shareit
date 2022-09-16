package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

   /**
    * Метод создания премета
    * @param item экземпляр сущности предмета, которые создается
    * @param user экземпляр сущности владельца предмета
    * @return экземпляр созданного предмета
    */
   Item createItem(Item item, Long user);

   /**
    * Метод обновления существующего предмета
    * @param item экземпляр сущности премета, который обновляется
    * @param itemId ID предмета
    * @param userId ID владельца предмета
    * @return экземпляр обновленного предмета
    */
   Item updateItem(Item item, Long itemId, Long userId);

   /**
    * Метод получения предмета по ID
    * @param itemId ID предмета
    * @return экзепляр сущности предмета
    */
   Item getItemById(Long itemId);

   /**
    * Метод получения предметов пользователя
    * @param userId ID владельца предемета
    * @return список экземпляров предметов пользователя
    */
   List<Item> retrieveAllItemByUserId(Long userId);

   /**
    * Метод поиска предмета по ключевому слову
    * @param keyword ключевое слово
    * @return список экземпляров предметов, которые содержат ключевое слово
    */
   List<Item> searchItemByKeyword(String keyword);
}
