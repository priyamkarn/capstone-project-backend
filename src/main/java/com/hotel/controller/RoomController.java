package com.hotel.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotel.model.Room;
import com.hotel.service.RoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Room>> getRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotelId(hotelId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @GetMapping("/hotel/{hotelId}/available")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @PathVariable Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam Integer guests) {
        return ResponseEntity.ok(roomService.getAvailableRooms(hotelId, checkIn, checkOut, guests));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Map<String, Integer>> getAvailableRoomCount(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        Integer count = roomService.getAvailableRoomCount(id, checkIn, checkOut);
        return ResponseEntity.ok(Map.of("availableRooms", count));
    }

    // IMPORTANT: pass hotelId as query param and use new service method
    @PostMapping
    public ResponseEntity<Room> createRoom(
            @RequestBody Room room,
            @RequestParam Long hotelId) { // e.g. /api/rooms?hotelId=4
        return ResponseEntity.ok(roomService.createRoom(room, hotelId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(Map.of("message", "Room deleted successfully"));
    }
}
