package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);
    List<Room> findByRoomType(String roomType);
    
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.pricePerNight BETWEEN :minPrice AND :maxPrice")
    List<Room> findByHotelIdAndPriceRange(
        @Param("hotelId") Long hotelId,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice
    );
    
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.maxOccupancy >= :guests")
    List<Room> findAvailableRoomsByCapacity(
        @Param("hotelId") Long hotelId,
        @Param("guests") Integer guests
    );
}