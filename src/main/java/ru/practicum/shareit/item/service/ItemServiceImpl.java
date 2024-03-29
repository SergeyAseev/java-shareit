package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
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
public class ItemServiceImpl implements ItemService {


    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));
        Item item = ItemMapper.toItem(itemDto, owner);

        validate(item);
        item.setOwner(owner);

        if (item.getRequest() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(item.getRequest().getId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
            item.setRequest(itemRequest);
        }
        log.info("Create Item with ID {}", item.getId());

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));
        Item item = ItemMapper.toItem(itemDto, owner);

        Item updatedItem = getItemValid(item, itemId, userId);
        log.info("Updated item with ID {}", itemId);
        itemRepository.save(updatedItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    @Transactional
    public ItemDtoWithBooking getItemById(Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID %s not found", itemId)));
        List<Comment> commentList = getCommentsByItemId(item);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime localDateTime = LocalDateTime.now();
            Booking lastBooking = bookingRepository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId, localDateTime);
            Booking nextBooking = bookingRepository.findTopByItem_IdAndStartAfterOrderByStartAsc(itemId, localDateTime);
            return ItemMapper.toItemDtoWithBooking(commentList, lastBooking, nextBooking, item);
        } else {
            return ItemMapper.toItemDtoWithBooking(commentList, null, null, item);
        }
    }

    @Transactional
    public List<ItemDtoWithBooking> retrieveAllItemByUserId(Long ownerId) {
        return itemRepository.findByOwnerIdOrderByIdAsc(ownerId)
                .stream()
                .map(item -> {
                            List<Comment> comments = getCommentsByItemId(item);
                            Booking lastBooking = bookingRepository
                                    .findFirstByItem_IdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
                            Booking nextBooking = bookingRepository
                                    .findTopByItem_IdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
                            return ItemMapper.toItemDtoWithBooking(comments, lastBooking, nextBooking, item);
                        }
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ItemDto> searchItemByKeyword(String keyword) {

        if (keyword.isBlank() || keyword.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByKeyword(keyword)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {

        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new ValidationException("Empty comment is prohibited");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID %s not found", itemId)));
        Comment comment = CommentMapper.toComment(commentDto, item, user);

        List<Booking> booking = bookingRepository.findByBookerIdStatePast(comment.getUser().getId(),
                LocalDateTime.now());

        if (booking.isEmpty()) {
            throw new ValidationException("User didn't booked anything");
        }

        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("Create Comment with ID {}", comment.getId());

        return CommentMapper.toCommentDto(comment);
    }

    public List<Comment> getCommentsByItemId(Item item) {
        return commentRepository.findByItem_IdOrderByCreatedDesc(item.getId());
    }

    private void validate(Item item) {
        if (item.getDescription() == null) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (item.getName().isBlank()) {
            throw new ValidationException("Название предмета не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Статус доступа не может быть пустым");
        }

    }

    private Item getItemValid(Item item, Long itemId, Long userId) {

        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID %s not found", itemId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID %s not found", userId)));

        if (user != null && !updatedItem.getOwner().getId().equals(userId))
            throw new NotFoundException("Предмет не доступен для брони");

        String updatedDescription = item.getDescription();
        if (updatedDescription != null && !updatedDescription.isBlank()) {
            updatedItem.setDescription(updatedDescription);
        }
        String updatedName = item.getName();
        if (updatedName != null && !updatedName.isBlank()) {
            updatedItem.setName(updatedName);
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return updatedItem;
    }
}
