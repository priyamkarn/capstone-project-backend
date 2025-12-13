package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.Hotel;
import com.hotel.service.HotelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    // ---------- SEARCH ----------
    @GetMapping("/search")
    public ResponseEntity<List<Hotel>> searchHotels(@RequestParam String query) {
        return ResponseEntity.ok(hotelService.searchHotels(query));
    }

    @GetMapping("/search/city")
    public ResponseEntity<List<Hotel>> searchByCity(@RequestParam String city) {
        return ResponseEntity.ok(hotelService.searchByCity(city));
    }

    @GetMapping("/search/landmark")
    public ResponseEntity<List<Hotel>> searchByLandmark(@RequestParam String landmark) {
        return ResponseEntity.ok(hotelService.searchByLandmark(landmark));
    }

    @GetMapping("/search/pincode")
    public ResponseEntity<List<Hotel>> searchByPinCode(@RequestParam String pinCode) {
        return ResponseEntity.ok(hotelService.searchByPinCode(pinCode));
    }

    // ---------- FILTER ----------
    @GetMapping("/filter/star-rating")
    public ResponseEntity<List<Hotel>> filterByStarRating(@RequestParam Integer rating) {
        return ResponseEntity.ok(hotelService.filterByStarRating(rating));
    }

    @GetMapping("/filter/property-type")
    public ResponseEntity<List<Hotel>> filterByPropertyType(@RequestParam String type) {
        return ResponseEntity.ok(hotelService.filterByPropertyType(type));
    }

    @GetMapping("/filter/min-rating")
    public ResponseEntity<List<Hotel>> filterByMinRating(@RequestParam Double rating) {
        return ResponseEntity.ok(hotelService.filterByMinRating(rating));
    }

    @PostMapping("/filter/amenities")
    public ResponseEntity<List<Hotel>> filterByAmenities(@RequestBody List<String> amenities) {
        return ResponseEntity.ok(hotelService.filterByAmenities(amenities));
    }

    // ---------- SORT (DB LEVEL) ----------
    @GetMapping("/sort/rating")
    public ResponseEntity<List<Hotel>> sortByRating(
            @RequestParam(defaultValue = "false") boolean ascending) {
        return ResponseEntity.ok(hotelService.sortByRating(ascending));
    }

    @GetMapping("/sort/price")
    public ResponseEntity<List<Hotel>> sortByPrice(
            @RequestParam(defaultValue = "true") boolean ascending) {
        return ResponseEntity.ok(hotelService.sortByPrice(ascending));
    }

    // ---------- CRUD ----------
    @PostMapping
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
        return ResponseEntity.ok(hotelService.createHotel(hotel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody Hotel hotel) {
        return ResponseEntity.ok(hotelService.updateHotel(id, hotel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.ok(Map.of("message", "Hotel deleted successfully"));
    }
    @GetMapping("/call_hotel")
public ResponseEntity<Map<String, String>> callHotel() {

    String message = """
            📞 Need help? You can call us 24/7 at 9680528733.
            Our team answers faster than a caffeinated squirrel! 🐿️

            🚪 For any gateman issues, email our vigilant gatekeepers:
            - Aditya (Senior Gate Guardian): aditya@gmail.com
            - Abin (Assistant Gate Guardian): abin@gmail.com
            They might just open the gate faster than you can say 'welcome!' 🚀

            🍳 For cooking disasters, contact our kitchen chaos controllers:
            - Chef Shresthi (Head of Not-Burning-Food): shresthi@gmail.com
            - SBajaj (Main Kitchen Boy & Emergency Food Rescuer): sbajaj@gmail.com
            Burnt rotis, salty curries, or mystery dishes —
            if it’s edible, they’ll save it. If not… they’ll laugh first. 😄
            """;

    return ResponseEntity.ok(Map.of(
            "success", "true",
            "message", message
    ));
}

}
