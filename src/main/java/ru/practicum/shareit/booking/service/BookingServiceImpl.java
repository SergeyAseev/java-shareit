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
                .orElseThrow(() -> new NotFoundException(String.format("Booking with ID %s doesn't exists",
                        bookingId)));

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
                .orElseThrow(() -> new NotFoundException(String.format("Booking with ID %s doesn't exists",
                        bookingId)));
        userService.getUserById(userId);

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException(String.format("Booking with ID %s wasn't find", bookingId));
        }
    }

    public List<BookingDto> getAllBookingByUserId(List<Booking> bookings, String stateStr) {

        BookingStateEnum state;
        List<Booking> bookingsReturn = new ArrayList<>();

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
                bookingsReturn = bookings;
                break;
            case WAITING:
            case REJECTED:
                BookingStatusEnum status = BookingStatusEnum.valueOf(state.toString());
                bookingsReturn = bookings.stream()
                        .filter(booking -> booking.getStatus().equals(status))
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookingsReturn = bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingsReturn = bookings.stream()
                        .filter(booking -> LocalDateTime.now().isBefore(booking.getStart()))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookingsReturn = bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getStart())
                                && LocalDateTime.now().isBefore(booking.getEnd()))
                        .collect(Collectors.toList());
                break;
        }

        return bookingsReturn
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingForOwner(Long userId, String stateStr) {

        userService.getUserById(userId);
        List<Booking> bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(userId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("There's no bookings");
        }
        return getAllBookingByUserId(bookings, stateStr);
    }

    @Override
    public List<BookingDto> getALlByBooker(Long userId, String stateStr) {

        userService.getUserById(userId);
        List<Booking> bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("There's no bookings");
        }
        return getAllBookingByUserId(bookings, stateStr);
    }

/*    @Override
    public Booking getLastBookingByItemId(Long itemId, Long userId) {
        userService.getUserById(userId);
        return bookingRepository.findFirstByItem_IdOrderByStartDesc(itemId);
    }

    @Override
    public Booking getNextBookingByItemId(Long itemId, Long userId) {
        userService.getUserById(userId);
        return bookingRepository.findTopByItem_IdOrderByStartDesc(itemId);;
    }*/

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
