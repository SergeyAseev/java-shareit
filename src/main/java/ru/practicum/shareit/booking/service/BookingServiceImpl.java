package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatusEnum;
import ru.practicum.shareit.exception.ExistsElementException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        User owner = bookingDto.getItem().getOwner();
        //User owner = UserMapper.toUser(userService.getUserById(bookingDto.getItem().getOwner().getId()));
        Item item = ItemMapper.toItem(itemService.getItemById(bookingDto.getId()), owner);
        User booker = UserMapper.toUser(userService.getUserById(userId));

        validate(item, owner, booker);

        Booking booking = new Booking(null, bookingDto.getStart(), bookingDto.getEnd(), item,
                booker, BookingStatusEnum.WAITING);
        log.info("Item with ID {} is booked", item.getId());

        if (bookingDto.getStart().after(bookingDto.getEnd())) {
            throw new ValidationException("bad times!");
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(Long bookingDto, Long userId, Boolean isApproved) {
        return null;
    }

    @Override
    public BookingDto getBookingById(Long bookingDto, Long userId) {
        return null;
    }

    @Override
    public List<BookingDto> getAllBookingByUserId(Long userId) {
        return null;
    }

    @Override
    public List<BookingDto> getAllBookingByUserId(Long userId, String state) {
        return null;
    }

    @Override
    public List<BookingDto> getAllBookingForOwner(Long userId) {
        return null;
    }

    @Override
    public List<BookingDto> getAllBookingForOwner(Long userId, String state) {
        return null;
    }

    private void validate(Item item, User owner, User booker) {

        if (!item.getAvailable()) {
            throw new ExistsElementException("Item is not available");
        }

        if (owner.getId().equals(booker.getId())) {
            throw new ValidationException("Owner doesn't have an opportunity to book own thing");
        }

    }
}
