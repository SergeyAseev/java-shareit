package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class BookingServiceTest {
    private final BookingRepository bookingRepository;

    private final BookingService bookingService;

    private final User user = new User(1L, "user1", "user1@mail.ru");
    private final User user2 = new User(2L, "user2", "user2@mail.ru");
    private final Item item = new Item(1L, user, "item1", "description1", true,
            null, null);
    private final Item itemNotAvailable = new Item(1L, user, "item1", "description1",
            false, null, null);
    private final Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
            item, user2, BookingStatus.WAITING);

    private final Booking bookingApproved = new Booking(2L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
            item, user2, BookingStatus.APPROVED);

    private final Booking bookingRejected = new Booking(3L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
            item, user2, BookingStatus.REJECTED);

    @Autowired
    public BookingServiceTest(BookingRepository bookingRepository,
                              BookingService bookingService,
                              ItemService itemService,
                              UserService userService) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        userService.createUser(UserMapper.toUserDto(user));
        userService.createUser(UserMapper.toUserDto(user2));
        itemService.createItem(ItemMapper.toItemDto(item), item.getOwner().getId());
        bookingRepository.save(booking);
        bookingRepository.save(bookingApproved);
        bookingRepository.save(bookingRejected);
    }

    @Test
    void getBookingByIdTest() {
        assertEquals(booking.getId(),
                bookingService.getBookingById(booking.getId(), booking.getBooker().getId()).getId());
    }

    @Test
    void getWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), 100L));
    }

    @Test
    void createBookingTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        Booking booking1 = BookingMapper.toBooking(user, item,
                bookingService.createBooking(bookingDto, 2L));
        assertEquals(booking1.getId(), bookingRepository.findById(booking1.getId()).orElse(null).getId());
    }

    @Test
    void createBookingStartInThePastTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();

        assertThrows(ValidationException.class, () -> BookingMapper.toBooking(user, item,
                bookingService.createBooking(bookingDto, 2L)));
    }

    @Test
    void createBookingStartAfterEndTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();

        assertThrows(ValidationException.class, () -> BookingMapper.toBooking(user, item,
                bookingService.createBooking(bookingDto, 2L)));
    }

    @Test
    void createBookingEndInThePastTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().minusDays(2))
                .itemId(1L)
                .build();

        assertThrows(ValidationException.class, () -> BookingMapper.toBooking(user, item,
                bookingService.createBooking(bookingDto, 2L)));
    }

    @Test
    void createBookingNotAvailableTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();

        assertThrows(NotFoundException.class, () -> BookingMapper.toBooking(user, itemNotAvailable,
                bookingService.createBooking(bookingDto, 1L)));
    }

    @Test
    void createBookingByOwnerTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();

        assertThrows(NotFoundException.class, () -> BookingMapper.toBooking(user, itemNotAvailable,
                bookingService.createBooking(bookingDto, 1L)));
    }

    @Test
    void updateBookingTest() {
        bookingService.updateBooking(booking.getId(), user.getId(), true);
        assertEquals(BookingStatus.APPROVED, bookingRepository.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void updateBooking2Test() {
        bookingService.updateBooking(booking.getId(), user.getId(), false);
        assertEquals(BookingStatus.REJECTED, bookingRepository.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void updateBookingAlreadyApprovedTest() {
        assertThrows(ValidationException.class, () -> bookingService
                .updateBooking(bookingApproved.getId(), user.getId(), true));
    }

    @Test
    void updateBookingAlreadyRejectedTest() {
        assertThrows(ValidationException.class, () -> bookingService
                .updateBooking(bookingRejected.getId(), user.getId(), false));
    }

    @Test
    void updateBookingApprovedByNotOwnerTest() {
        assertThrows(NotFoundException.class, () -> bookingService
                .updateBooking(booking.getId(), user2.getId(), true));
    }

    @Test
    void getAllBookingByUserIdTest() {
        assertEquals(3,
                bookingService.getAllBookingByUserId(user2.getId(), "ALL", 0, 10).size());
    }

    @Test
    void getPastBookingByUserIdTest() {
        assertEquals(new ArrayList<>(),
                bookingService.getAllBookingByUserId(user2.getId(), "PAST", 0, 10));
    }

    @Test
    void getFutureBookingByUserIdTest() {
        assertEquals(new ArrayList<>(),
                bookingService.getAllBookingByUserId(user2.getId(), "FUTURE", 0, 10));
    }

    @Test
    void getCurrentBookingByUserIdTest() {
        assertEquals(3,
                bookingService.getAllBookingByUserId(user2.getId(), "CURRENT", 0, 10).size());
    }

    @Test
    void getWaitingBookingByUserIdTest() {
        assertEquals(List.of(booking).get(0).getId(),
                bookingService.getAllBookingByUserId(user2.getId(), "WAITING", 0, 10).get(0).getId());
    }

    @Test
    void getRejectedBookingByUserIdTest() {
        assertEquals(List.of(bookingRejected).get(0).getId(),
                bookingService.getAllBookingByUserId(user2.getId(), "REJECTED", 0, 10).get(0).getId());
    }

    @Test
    void getAllBookingByUserIdNegativeTest() {
        assertThrows(IllegalArgumentException.class, () -> bookingService
                .getAllBookingByUserId(user2.getId(), "ALL", -1, -1));
    }

    @Test
    void getAllBookingByUserIdBadWithoutBookingTest() {
        assertThrows(NotFoundException.class, () -> bookingService
                .getAllBookingByOwnerId(user2.getId(), "BAD_STATE", 0, 10).get(0).getId());

    }

    @Test
    void getAllBookingByUserIdBadStateTest() {
        assertThrows(ValidationException.class, () -> bookingService
                .getAllBookingByUserId(user2.getId(), "BAD_STATE", 0, 10).get(0).getId());

    }

    @Test
    void getAllBookingByOwnerIdTest() {
        assertEquals(3,
                bookingService.getAllBookingByOwnerId(user.getId(), "ALL", 0, 10).size());
    }

    @Test
    void getPastBookingByOwnerIdTest() {
        assertEquals(new ArrayList<>(),
                bookingService.getAllBookingByOwnerId(user.getId(), "PAST", 0, 10));
    }

    @Test
    void getFutureBookingByOwnerIdTest() {
        assertEquals(new ArrayList<>(),
                bookingService.getAllBookingByOwnerId(user.getId(), "FUTURE", 0, 10));
    }

    @Test
    void getCurrentBookingByOwnerIdTest() {
        assertEquals(3,
                bookingService.getAllBookingByOwnerId(user.getId(), "CURRENT", 0, 10).size());
    }

    @Test
    void getWaitingBookingByOwnerIdTest() {
        assertEquals(List.of(booking).get(0).getId(),
                bookingService.getAllBookingByOwnerId(user.getId(), "WAITING", 0, 10).get(0).getId());
    }

    @Test
    void getRejectedBookingByOwnerIdTest() {
        assertEquals(List.of(bookingRejected).get(0).getId(),
                bookingService.getAllBookingByOwnerId(user.getId(), "REJECTED", 0, 10).get(0).getId());
    }

    @Test
    void getAllBookingByOwnerIdNegativeTest() {
        assertThrows(ValidationException.class, () -> bookingService
                .getAllBookingByOwnerId(user.getId(), "ALL", -1, -1));
    }

    @Test
    void getAllBookingByOwnerIdBadStateTest() {
        assertThrows(ValidationException.class, () -> bookingService
                .getAllBookingByOwnerId(user.getId(), "BAD_STATE", 0, 10).get(0).getId());

    }

    @Test
    void getAllBookingByOwnerIdNULLStateTest() {
        assertEquals(3,
                bookingService.getAllBookingByOwnerId(user.getId(), null, 0, 10).size());
    }
}