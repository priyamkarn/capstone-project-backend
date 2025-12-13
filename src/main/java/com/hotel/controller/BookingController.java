package com.hotel.controller;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.hotel.model.Room;
import com.hotel.repository.RoomRepository;
import com.hotel.service.BookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {
    
    private final BookingService bookingService;
    private final RoomRepository roomRepository;
    
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
            
            if (request.get("gstNumber") != null) {
                booking.setGstNumber((String) request.get("gstNumber"));
            }
            if (request.get("specialRequests") != null) {
                booking.setSpecialRequests((String) request.get("specialRequests"));
            }
            
            // Extract rooms data and FETCH Room entities
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> roomsData = (List<Map<String, Object>>) request.get("rooms");
            List<BookingRoom> bookingRooms = new ArrayList<>();
            
            for (Map<String, Object> roomData : roomsData) {
                // Get roomId and numberOfRooms
                Long roomId = ((Number) roomData.get("roomId")).longValue();
                Integer numberOfRooms = (Integer) roomData.get("numberOfRooms");
                
                // CRITICAL FIX: Fetch the actual Room entity from database
                Room room = roomRepository.findById(roomId)
                        .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
                
                // Create BookingRoom with the actual Room entity
                BookingRoom bookingRoom = new BookingRoom();
                bookingRoom.setRoom(room);  // Set the complete Room entity
                bookingRoom.setNumberOfRooms(numberOfRooms);
                
                bookingRooms.add(bookingRoom);
                
                // Set hotel from first room if not set
                if (booking.getHotel() == null) {
                    booking.setHotel(room.getHotel());
                }
            }
            
            Booking savedBooking = bookingService.createBooking(booking, bookingRooms);
            return ResponseEntity.ok(savedBooking);
        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Apply promo code to booking
     * POST /api/bookings/1/apply-promo
     * Body: {"promoCode": "WELCOME20"}
     */
    @PostMapping("/{bookingId}/apply-promo")
    public ResponseEntity<?> applyPromoCode(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> request) {
        try {
            String promoCode = request.get("promoCode");
            Booking booking = bookingService.applyPromoCode(bookingId, promoCode);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Promo code applied successfully",
                "booking", booking,
                "discountAmount", booking.getDiscountAmount(),
                "finalPrice", booking.getFinalPrice()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * Remove promo code from booking
     * POST /api/bookings/1/remove-promo
     */
    @PostMapping("/{bookingId}/remove-promo")
    public ResponseEntity<?> removePromoCode(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingService.removePromoCode(bookingId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Promo code removed",
                "booking", booking,
                "finalPrice", booking.getFinalPrice()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * Apply wallet credit to booking
     * POST /api/bookings/1/apply-wallet
     * Body: {"amount": 1000.0}
     */
    @PostMapping("/{bookingId}/apply-wallet")
    public ResponseEntity<?> applyWalletCredit(
            @PathVariable Long bookingId,
            @RequestBody Map<String, Double> request) {
        try {
            Double amount = request.get("amount");
            Booking booking = bookingService.applyWalletCredit(bookingId, amount);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Wallet credit applied successfully",
                "booking", booking,
                "walletCreditUsed", amount,
                "totalDiscount", booking.getDiscountAmount(),
                "finalPrice", booking.getFinalPrice()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
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

    @GetMapping("/{id}/cancellation-fee")
public ResponseEntity<?> getCancellationFee(@PathVariable Long id) {
    try {
        double fee = bookingService.calculateCancellationFee(id);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "cancellationFee", fee
        ));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of(
            "success", false,
            "error", e.getMessage()
        ));
    }
}
}