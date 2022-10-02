package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
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

    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID %s not found", bookingDto.getItemId())));
        Booking booking = BookingMapper.toBooking(booker, item, bookingDto);

        validate(booking);
        booking.setStatus(BookingStatus.WAITING);
        log.info("Item with ID {} is booked", item.getId());

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto updateBooking(Long bookingId, Long userId, Boolean isApproved) {
        Booking booking = getBookingById(bookingId);

        validateForUpdating(booking, userId, isApproved);
        setApprovedStatus(booking, isApproved);
        log.info("Booking with ID {} is updated", booking.getId());

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException(String.format("Booking with ID %s wasn't find", bookingId));
        }
    }

    @Transactional
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with ID %s doesn't exists",
                        bookingId)));
    }

    public List<Booking> getAllUser(long useId) {
        userRepository.findById(useId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", useId)));
        return bookingRepository.findAllByBooker_IdOrderByStartDesc(useId);
    }

    public BookingState getStateByStr(String stateStr) {

        BookingState state;
        if (stateStr == null) {
            state = BookingState.ALL;
        } else {
            try {
                state = BookingState.valueOf(stateStr);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(String.format("Unknown state: %s", stateStr));
            }
        }

        return state;
    }

    public List<BookingDto> getAllBookingByUser(Long useId, String stateStr) {

        BookingState state = getStateByStr(stateStr);
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = getAllUser(useId);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdStatePast(useId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(useId, BookingStatus.WAITING);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdStateCurrent(useId, now);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatus(useId, BookingStatus.REJECTED);
                break;
            case FUTURE:
                bookings = bookingRepository.findFuture(useId, now);
                break;
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getAllBookingByOwner(Long useId, String stateStr) {

        BookingState state = getStateByStr(stateStr);
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findOwnerAll(useId);
                break;
            case FUTURE:
                bookings = bookingRepository.findOwnerFuture(useId, now);
                break;
            case CURRENT:
                bookings = bookingRepository.findOwnerCurrent(useId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(useId, BookingStatus.WAITING);
                break;
            case PAST:
                bookings = bookingRepository.findOwnerPast(useId, now);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(useId, BookingStatus.REJECTED);
                break;
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BookingDto> getAllBookingByOwnerId(Long ownerId, String stateStr) {

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", ownerId)));
        List<Booking> bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("There's no bookings");
        }
        return getAllBookingByOwner(ownerId, stateStr);
    }

    @Transactional
    public List<BookingDto> getAllBookingByUserId(Long userId, String stateStr) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));
        List<Booking> bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("There's no bookings");
        }
        return getAllBookingByUser(userId, stateStr);
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

    private void validateForUpdating(Booking booking, Long userId, Boolean isApproved) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("User != owner");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) && isApproved) {
            throw new ValidationException("Already approved");
        }
        if (booking.getStatus().equals(BookingStatus.REJECTED) && !isApproved) {
            throw new ValidationException("Already rejected");
        }
    }

    private void setApprovedStatus(Booking booking, Boolean isApproved) {
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
    }
}
