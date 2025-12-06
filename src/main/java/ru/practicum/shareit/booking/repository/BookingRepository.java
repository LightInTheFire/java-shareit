package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //booker methods
    // ALL
    List<Booking> findByBookerIdOrderByStartTimeDesc(Long bookerId);

    // CURRENT
    @Query("""
                SELECT b FROM Booking b
                WHERE b.booker.id = :userId
                  AND :now BETWEEN b.startTime AND b.endTime
                ORDER BY b.startTime DESC
            """)
    List<Booking> findCurrentByBooker(
            Long userId,
            LocalDateTime now
    );

    // PAST
    List<Booking> findByBookerIdAndEndTimeBeforeOrderByStartTimeDesc(
            Long bookerId,
            LocalDateTime now
    );

    // FUTURE
    List<Booking> findByBookerIdAndStartTimeAfterOrderByStartTimeDesc(
            Long bookerId,
            LocalDateTime now
    );

    // WAITING / REJECTED
    List<Booking> findByBookerIdAndStatusOrderByStartTimeDesc(
            Long bookerId,
            BookingStatus status
    );

    //owner methods
    // ALL
    List<Booking> findByItemOwnerIdOrderByStartTimeDesc(Long ownerId);

    // CURRENT
    @Query("""
                SELECT b FROM Booking b
                WHERE b.item.owner.id = :ownerId
                  AND :now BETWEEN b.startTime AND b.endTime
                ORDER BY b.startTime DESC
            """)
    List<Booking> findCurrentByOwner(
            Long ownerId,
            LocalDateTime now
    );

    // PAST
    List<Booking> findByItemOwnerIdAndEndTimeBeforeOrderByStartTimeDesc(
            Long ownerId,
            LocalDateTime now
    );

    // FUTURE
    List<Booking> findByItemOwnerIdAndStartTimeAfterOrderByStartTimeDesc(
            Long ownerId,
            LocalDateTime now
    );

    // WAITING / REJECTED
    List<Booking> findByItemOwnerIdAndStatusOrderByStartTimeDesc(
            Long ownerId,
            BookingStatus status
    );
}
