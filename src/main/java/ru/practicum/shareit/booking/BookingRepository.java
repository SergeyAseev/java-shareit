package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByIdDesc(Long userId);

    List<Booking> findByBooker_IdAndStartAfterAndEndBefore(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBooker_IdAndEndBefore(Long userId, LocalDateTime localDateTime);

    List<Booking> findByBooker_IdAndStartAfter(Long userId, LocalDateTime localDateTime);

    List<Booking> findByBooker_IdAndStatus(Long userId, BookingStatusEnum bookingStatusEnum);
}
