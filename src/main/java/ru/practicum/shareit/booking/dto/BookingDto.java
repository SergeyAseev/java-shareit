package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatusEnum;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;



//@StartBeforeEnd
@Getter
@Setter
@AllArgsConstructor
public class BookingDto {

    private Long id;

    private Date start;

    private Date end;

    private Item item;

    private User booker;

    private BookingStatusEnum status;
}
