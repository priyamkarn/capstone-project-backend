package com.hotel.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.Booking;
import com.hotel.model.BookingRoom;
import com.hotel.service.BookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {
    
    private final BookingService bookingService;
    
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> request) {
        try {
            // Extract booking data
            Booking booking = new Booking();
            booking.setCheckInDate(LocalDate.parse((String) request.get("checkInDate")));
            booking.setCheckOutDate(LocalDate.parse((String) request.get("checkOutDate")));
            booking.setTotalGuests((Integer) request.get("totalGuests"));
            booking.setGuestName((String) request.get("guestName"));
            booking.setGuestEmail((String) request.get("guestEmail"));
            booking.setGuestPhone((String) request.get("guestPhone"));
            booking.setEmergencyContact((String) request.get("emergencyContact"));
            booking.setGstNumber((String) request.get("gstNumber"));
            booking.setSpecialRequests((String) request.get("specialRequests"));
            
            // Extract rooms data
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> roomsData = (List<Map<String, Object>>) request.get("rooms");
            List<BookingRoom> rooms = roomsData.stream()
                .map(r -> {
                    BookingRoom br = new BookingRoom();
                    br.setNumberOfRooms((Integer) r.get("numberOfRooms"));
                    // Room will be set in service
                    return br;
                })
                .toList();
            
            Booking savedBooking = bookingService.createBooking(booking, rooms);
            return ResponseEntity.ok(savedBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }
    
    @GetMapping("/reference/{reference}")
    public ResponseEntity<Booking> getBookingByReference(@PathVariable String reference) {
        return ResponseEntity.ok(bookingService.getBookingByReference(reference));
    }
    
    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getCurrentUserBookings());
    }
    
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Booking>> getHotelBookings(@PathVariable Long hotelId) {
        return ResponseEntity.ok(bookingService.getHotelBookings(hotelId));
    }
    
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.confirmBooking(id));
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.cancelBooking(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/modify")
    public ResponseEntity<?> modifyBooking(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        try {
            Booking booking = bookingService.modifyBooking(id, checkIn, checkOut);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/check-in")
    public ResponseEntity<Booking> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.checkIn(id));
    }
    
    @PutMapping("/{id}/check-out")
    public ResponseEntity<Booking> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.checkOut(id));
    }
    
    @PostMapping("/{id}/request-upgrade")
    public ResponseEntity<?> requestUpgrade(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Upgrade request submitted"));
    }
    
    @PostMapping("/{id}/request-early-checkin")
    public ResponseEntity<?> requestEarlyCheckIn(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Early check-in request submitted"));
    }
    
    @PostMapping("/{id}/request-late-checkout")
    public ResponseEntity<?> requestLateCheckOut(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", "Late check-out request submitted"));
    }
}
