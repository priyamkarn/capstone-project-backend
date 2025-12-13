package com.hotel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.Hotel;
import com.hotel.repository.HotelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
    }

    // ---------- SEARCH ----------
    public List<Hotel> searchHotels(String search) {
        return hotelRepository.searchHotels(search);
    }

    public List<Hotel> searchByCity(String city) {
        return hotelRepository.findByCityIgnoreCase(city);
    }

    public List<Hotel> searchByLandmark(String landmark) {
        return hotelRepository.findByLandmarkContainingIgnoreCase(landmark);
    }

    public List<Hotel> searchByPinCode(String pinCode) {
        return hotelRepository.findByPinCode(pinCode);
    }

    // ---------- FILTER ----------
    public List<Hotel> filterByStarRating(Integer rating) {
        return hotelRepository.findByStarRating(rating);
    }

    public List<Hotel> filterByPropertyType(String type) {
        return hotelRepository.findByPropertyType(
                Hotel.PropertyType.valueOf(type.toUpperCase()));
    }

    public List<Hotel> filterByMinRating(Double minRating) {
        return hotelRepository.findByMinimumRating(minRating);
    }

    public List<Hotel> filterByAmenities(List<String> amenities) {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getAmenities().containsAll(amenities))
                .collect(Collectors.toList());
    }

    // ---------- SORT (DB LEVEL) ----------
    public List<Hotel> sortByRating(boolean ascending) {
        return ascending
                ? hotelRepository.findAllByOrderByAverageRatingAsc()
                : hotelRepository.findAllByOrderByAverageRatingDesc();
    }

    public List<Hotel> sortByPrice(boolean ascending) {
        return ascending
                ? hotelRepository.sortByPriceAsc()
                : hotelRepository.sortByPriceDesc();
    }

    // ---------- CRUD ----------
    @Transactional
    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    @Transactional
    public Hotel updateHotel(Long id, Hotel data) {
        Hotel hotel = getHotelById(id);

        if (data.getName() != null) hotel.setName(data.getName());
        if (data.getCity() != null) hotel.setCity(data.getCity());
        if (data.getStarRating() != null) hotel.setStarRating(data.getStarRating());

        return hotelRepository.save(hotel);
    }

    @Transactional
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }
}
