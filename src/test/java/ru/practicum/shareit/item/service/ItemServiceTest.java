package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemServiceTest {
    private final CommentRepository commentRepository;

    private final ItemRepository itemRepository;

    private final BookingService bookingService;

    private final BookingRepository bookingRepository;

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final User user = new User(1L, "Simple User", "user@mail.ru");
    private final User user2 = new User(2L, "Another User", "test@mail.ru");
    private final Item item = new Item(1L, user, "Unit", "Super unit", true,
            null, null);

    private final Item item1;
    private final ItemDtoWithBooking itemCommentDto;

    private final LocalDateTime localDateTime = LocalDateTime.now();

    @Autowired
    public ItemServiceTest(CommentRepository commentRepository,
                           UserRepository userRepository,
                           ItemRepository itemRepository,
                           BookingService bookingService,
                           BookingRepository bookingRepository,
                           ItemService itemService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        userRepository.save(user);
        userRepository.save(user2);
        this.itemRepository = itemRepository;
        this.itemService = itemService;
        item1 = item;
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        itemCommentDto = ItemMapper.toItemDtoWithBooking(new ArrayList<>(),
                bookingRepository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(item1.getId(), localDateTime),
                bookingRepository.findTopByItem_IdAndStartAfterOrderByStartAsc(item1.getId(), localDateTime),
                item);


    }

    @Test
    void getItemById() {
        ItemDto item1 = itemService.createItem(ItemMapper.toItemDto(item),
                item.getOwner().getId());
        assertEquals(itemCommentDto.getId(), itemService.getItemById(item1.getId(), user.getId()).getId());
    }

    @Test
    void getAllByUserId() {
        assertEquals(List.of(itemCommentDto).get(0).getId(),
                itemService.retrieveAllItemByUserId(item1.getOwner().getId()).get(0).getId());
    }


    @Test
    void createItemDto() {
        ItemDto item2 = itemService.createItem(ItemMapper.toItemDto(item1),
                item1.getOwner().getId());
        assertEquals(itemRepository.findById(item1.getId()).orElse(null).getId(), item2.getId());
    }

    @Test
    void searchItemByText() {
        assertEquals(List.of(ItemMapper.toItemDto(item1)).get(0).getId(),
                itemService.searchItemByKeyword("unit").get(0).getId());
    }

    @Test
    void addComment() throws InterruptedException {
        itemService.createItem(ItemMapper.toItemDto(item),
                item.getOwner().getId());

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item1.getId())
                .build();
        Booking booking = BookingMapper.toBooking(user2, item1, bookingService.createBooking(
                bookingDto, user2.getId()));
        BookingDto updatedBooking = bookingService.updateBooking(booking.getId(), user.getId(), true);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");
        Thread.sleep(2000);
        CommentDto comment = itemService.addComment(
                item1.getId(),
                user2.getId(),
                commentDto
        );
        assertEquals(commentRepository.findById(comment.getId()).orElse(null).getText(), comment.getText());
    }
}