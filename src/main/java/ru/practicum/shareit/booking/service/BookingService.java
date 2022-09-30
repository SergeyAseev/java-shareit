package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    /**
     * Метод создания бронирования вещи
     *
     * @param bookingDto экземпляр сущности bookingDto, которая создается
     * @param userId     ID того, кто бронирует
     * @return экземпляр созданного бронирования
     */
    BookingDto createBooking(BookingDto bookingDto, Long userId);

    /**
     * Метод обновления бронирования (подтверждение или отклонение)
     *
     * @param bookingDto экземпляр сущности bookingDto, которая создается
     * @param userId     ID владельца вещи
     * @param isApproved флаг подтверждения брони
     * @return экземпляр обновленного бронирования
     */
    BookingDto updateBooking(Long bookingDto, Long userId, Boolean isApproved);

    /**
     * Метод получения бронирования по ID
     *
     * @param bookingId ID бронирования
     * @param userId    ID пользователя
     * @return экземляр сущности бронирования
     */
    BookingDto getBookingById(Long bookingId, Long userId);

    /**
     * Метод получения всех бронирований для владельца
     *
     * @param userId ID владельца вещи
     * @param state  статус брони по времени и подтверждению(?)
     * @return список экземляров бронирований
     */
    List<BookingDto> getAllBookingForOwner(Long userId, String state);


    /**
     * Метод получения всех бронирований для пользователя
     *
     * @param userId ID того, кто бронирует
     * @param state  state статус брони по времени и подтверждению(?)
     * @return список экземляров бронирований
     */
    List<BookingDto> getALlByBooker(Long userId, String state);
}
