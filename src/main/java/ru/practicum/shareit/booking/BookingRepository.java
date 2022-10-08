package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);

    Booking findFirstByItem_IdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    Booking findTopByItem_IdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime start);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :id AND b.end < :currentTime AND upper(b.status) = UPPER('APPROVED')" +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerIdStatePast(@Param("id") long id, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :useId AND b.end >= :currentTime AND :currentTime >= b.start " +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerIdStateCurrent(@Param("useId") long useId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :currentTime ORDER BY b.start DESC")
    List<Booking> findFuture(@Param("userId") long useId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findOwnerAll(long ownerId);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE  i.owner.id = :userId AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnerFuture(@Param("userId") long userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId " +
            "AND b.start <= :currentTime AND b.end >= :currentTime ORDER BY b.start DESC ")
    List<Booking> findOwnerCurrent(@Param("userId") long userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId AND b.end < :currentTime")
    List<Booking> findOwnerPast(@Param("userId") long userId, @Param("currentTime") LocalDateTime currentTime);
}
