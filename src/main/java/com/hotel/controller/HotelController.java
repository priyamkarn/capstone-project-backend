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
}
