package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateEnum;
import ru.practicum.shareit.booking.model.BookingStatusEnum;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        User booker = UserMapper.toUser(userService.getUserById(userId));
        Item item = itemService.getItemByIdForBooking(bookingDto.getItemId());
        Booking booking = BookingMapper.toBooking(booker, item, bookingDto);


        validate(booking);
        booking.setStatus(BookingStatusEnum.WAITING);
        log.info("Item with ID {} is booked", item.getId());

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(Long bookingId, Long userId, Boolean isApproved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow( () -> new NotFoundException(String.format("Booking with ID %s doesn't exists",
                        bookingId)));

        User booker = UserMapper.toUser(userService.getUserById(userId));

        //validate(booking);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("User != owner");
        }
        if (booking.getStatus().equals(BookingStatusEnum.APPROVED) && isApproved) {
            throw new ValidationException("Already approved");
        }
        if (booking.getStatus().equals(BookingStatusEnum.REJECTED) && !isApproved) {
            throw new ValidationException("Already rejected");
        }
        if (isApproved) {
            booking.setStatus(BookingStatusEnum.APPROVED);
        } else {
            booking.setStatus(BookingStatusEnum.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow( () -> new NotFoundException(String.format("Booking with ID %s doesn't exists",
                        bookingId)));
        userService.getUserById(userId);

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException(String.format("Booking with ID %s wasn't find", bookingId));
        }
    }

    @Override
    public List<BookingDto> getAllBookingByUserId(Long userId, String stateStr) {

        userService.getUserById(userId);
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        BookingStateEnum state;

        if (stateStr == null) {
            state = BookingStateEnum.ALL;
        } else {
            try {
                state = BookingStateEnum.valueOf(stateStr);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(String.format("Unknown state: %s", stateStr));
            }
        }

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_IdOrderByIdDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartAfterAndEndBefore(userId, localDateTime, localDateTime);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndBefore(userId, localDateTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartAfter(userId, localDateTime);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatusEnum.WAITING);
                break;
        }

        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingForOwner(Long userId, String state) {

        userService.getUserById(userId);
        return bookingRepository.findByBooker_IdOrderByIdDesc(userId)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void validate(Booking booking) {

        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Item isn't available");
        }
        if (booking.getBooker().getId().equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Owner can not booked his item");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Start after end");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start in the past");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("End in the past");
        }

    }
}
