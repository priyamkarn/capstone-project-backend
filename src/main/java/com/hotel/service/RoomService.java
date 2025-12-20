package com.hotel.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.Hotel;
import com.hotel.model.Room;
import com.hotel.repository.BookingRoomRepository;
import com.hotel.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRoomRepository bookingRoomRepository;
    private final HotelService hotelService; // <-- inject HotelService

    public List<Room> getRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    public List<Room> getAvailableRooms(Long hotelId, LocalDate checkIn, LocalDate checkOut, Integer guests) {
        List<Room> rooms = roomRepository.findAvailableRoomsByCapacity(hotelId, guests);

        return rooms.stream()
            .filter(room -> {
                Integer bookedRooms = bookingRoomRepository.countBookedRooms(room.getId(), checkIn, checkOut);
                return room.getTotalRooms() > bookedRooms;
            })
            .toList();
    }

    public Integer getAvailableRoomCount(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        Room room = getRoomById(roomId);
        Integer bookedRooms = bookingRoomRepository.countBookedRooms(roomId, checkIn, checkOut);
        return room.getTotalRooms() - bookedRooms;
    }

    @Transactional
    public Room createRoom(Room room, Long hotelId) {
        // load hotel and attach it so hotel_id is NOT NULL
        Hotel hotel = hotelService.getHotelById(hotelId);
        room.setHotel(hotel);
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(Long id, Room roomData) {
        Room room = getRoomById(id);

        if (roomData.getRoomType() != null) room.setRoomType(roomData.getRoomType());
        if (roomData.getDescription() != null) room.setDescription(roomData.getDescription());
        if (roomData.getPricePerNight() != null) room.setPricePerNight(roomData.getPricePerNight());
        if (roomData.getMaxOccupancy() != null) room.setMaxOccupancy(roomData.getMaxOccupancy());
        if (roomData.getTotalRooms() != null) room.setTotalRooms(roomData.getTotalRooms());

        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
