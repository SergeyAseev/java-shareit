package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .owner(user)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }


    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(List<Comment> commentList, Booking lastBooking,
                                                          Booking nextBooking, Item item) {

        List<ItemDtoWithBooking.Comment> comments = commentList.stream()
                .map(comment -> {
                    ItemDtoWithBooking.Comment comment1 = new ItemDtoWithBooking.Comment();
                    comment1.setId(comment.getId());
                    comment1.setText(comment.getText());
                    comment1.setAuthorName(comment.getUser().getName());
                    comment1.setCreated(comment.getCreated());
                    return comment1;
                }).collect(Collectors.toList());

        ItemDtoWithBooking.Booking lstBooking = new ItemDtoWithBooking.Booking();
        if (lastBooking != null) {
            lstBooking.setId(lastBooking.getId());
            lstBooking.setBookerId(lastBooking.getBooker().getId());
        } else {
            lstBooking = null;
        }

        ItemDtoWithBooking.Booking nxtBooking = new ItemDtoWithBooking.Booking();
        if (nextBooking != null) {
            nxtBooking.setId(nextBooking.getId());
            nxtBooking.setBookerId(nextBooking.getBooker().getId());
        } else {
            nxtBooking = null;
        }


        return ItemDtoWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lstBooking)
                .nextBooking(nxtBooking)
                .comments(comments)
                .build();

    }
}
