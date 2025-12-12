package com.hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.model.BookingRoom;

@Repository
public interface BookingRoomRepository extends JpaRepository<BookingRoom, Long> {
    List<BookingRoom> findByBookingId(Long bookingId);
    List<BookingRoom> findByRoomId(Long roomId);
    
    @Query("SELECT COALESCE(SUM(br.numberOfRooms), 0) FROM BookingRoom br " +
           "WHERE br.room.id = :roomId " +
           "AND br.booking.checkInDate <= :checkOutDate " +
           "AND br.booking.checkOutDate >= :checkInDate " +
           "AND br.booking.status NOT IN ('CANCELLED', 'NO_SHOW')")
    Integer countBookedRooms(
        @Param("roomId") Long roomId,
        @Param("checkInDate") LocalDate checkInDate,
        @Param("checkOutDate") LocalDate checkOutDate
    );
}