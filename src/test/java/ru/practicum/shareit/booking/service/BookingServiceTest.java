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
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
    private final Item item = new Item(1L, user, "item1", "description1", true, null, null);
    private final Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
            item, user2, BookingStatus.WAITING);

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
    void updateBookingTest() {
        bookingService.updateBooking(booking.getId(), user.getId(), true);
        assertEquals(BookingStatus.APPROVED, bookingRepository.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void getAllBookingByUserIdTest() {
        assertEquals(List.of(booking).get(0).getId(),
                bookingService.getAllBookingByUserId(user2.getId(), "ALL", 0, 10).get(0).getId());
    }

    @Test
    void getAllBookingByOwnerIdTest() {
        assertEquals(List.of(booking).get(0).getId(),
                bookingService.getAllBookingByOwnerId(user.getId(), "ALL", 0, 10).get(0).getId());
    }
}