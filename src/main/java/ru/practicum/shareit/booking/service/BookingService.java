package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    /**
     *
     * @param bookingDto
     * @param userId
     * @return
     */
    BookingDto createBooking(BookingDto bookingDto, Long userId);

    /**
     *
     * @param bookingDto
     * @param userId
     * @param isApproved
     * @return
     */
    BookingDto updateBooking(Long bookingDto, Long userId, Boolean isApproved);

    /**
     *
     * @param bookingDto
     * @param userId
     * @return
     */
    BookingDto getBookingById(Long bookingDto, Long userId);

    /**
     *
     * @param userId
     * @return
     */
    List<BookingDto> getAllBookingByUserId(Long userId);

    /**
     *
     * @param userId
     * @param state
     * @return
     */
    List<BookingDto> getAllBookingByUserId(Long userId, String state);

    /**
     *
     * @param userId
     * @return
     */
    List<BookingDto> getAllBookingForOwner(Long userId);

    /**
     *
     * @param userId
     * @param state
     * @return
     */
    List<BookingDto> getAllBookingForOwner(Long userId, String state);

}
