package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

/*    List<Booking> findByBooker_IdOrderByIdDesc(Long userId);

    List<Booking> findByBooker_IdAndStartAfterAndEndBefore(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBooker_IdAndEndBefore(Long userId, LocalDateTime localDateTime);

    List<Booking> findByBooker_IdAndStartAfter(Long userId, LocalDateTime localDateTime);

    List<Booking> findByBooker_IdAndStatus(Long userId, BookingStatusEnum bookingStatusEnum);*/

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);

    Booking findByBooker_IdAndEndBefore(Long userId, LocalDateTime end);

    Booking findFirstByItem_IdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Booking findTopByItem_IdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime start);
}
