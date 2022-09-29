package ru.practicum.shareit.booking.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {


    @Autowired
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody @Valid BookingDto bookingDto) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId, @PathParam("approved") @NonNull Boolean approved) {
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathParam("state") String state) {
        return bookingService.getAllBookingByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathParam("state") String state) {
        return bookingService.getAllBookingForOwner(userId, state);
    }

}
