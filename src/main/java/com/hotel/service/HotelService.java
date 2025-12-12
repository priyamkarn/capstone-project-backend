package com.hotel.service;

import java.util.Comparator;
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
    
    public List<Hotel> filterByStarRating(Integer starRating) {
        return hotelRepository.findByStarRating(starRating);
    }
    
    public List<Hotel> filterByPropertyType(String propertyType) {
        Hotel.PropertyType type = Hotel.PropertyType.valueOf(propertyType.toUpperCase());
        return hotelRepository.findByPropertyType(type);
    }
    
    public List<Hotel> filterByMinRating(Double minRating) {
        return hotelRepository.findByMinimumRating(minRating);
    }
    
    public List<Hotel> filterByAmenities(List<String> amenities) {
        return hotelRepository.findAll().stream()
            .filter(hotel -> hotel.getAmenities().containsAll(amenities))
            .collect(Collectors.toList());
    }
    
    public List<Hotel> sortByPrice(List<Hotel> hotels, boolean ascending) {
        return hotels.stream()
            .sorted(ascending ? 
                Comparator.comparing(h -> h.getRooms().stream()
                    .mapToDouble(r -> r.getPricePerNight())
                    .min().orElse(Double.MAX_VALUE)) :
                Comparator.comparing((Hotel h) -> h.getRooms().stream()
                    .mapToDouble(r -> r.getPricePerNight())
                    .min().orElse(Double.MAX_VALUE)).reversed())
            .collect(Collectors.toList());
    }
    
    public List<Hotel> sortByRating(List<Hotel> hotels, boolean ascending) {
        return hotels.stream()
            .sorted(ascending ? 
                Comparator.comparing(Hotel::getAverageRating) :
                Comparator.comparing(Hotel::getAverageRating).reversed())
            .collect(Collectors.toList());
    }
    
    @Transactional
    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }
    
    @Transactional
    public Hotel updateHotel(Long id, Hotel hotelData) {
        Hotel hotel = getHotelById(id);
        
        if (hotelData.getName() != null) hotel.setName(hotelData.getName());
        if (hotelData.getDescription() != null) hotel.setDescription(hotelData.getDescription());
        if (hotelData.getCity() != null) hotel.setCity(hotelData.getCity());
        if (hotelData.getLandmark() != null) hotel.setLandmark(hotelData.getLandmark());
        if (hotelData.getPinCode() != null) hotel.setPinCode(hotelData.getPinCode());
        if (hotelData.getAddress() != null) hotel.setAddress(hotelData.getAddress());
        if (hotelData.getLatitude() != null) hotel.setLatitude(hotelData.getLatitude());
        if (hotelData.getLongitude() != null) hotel.setLongitude(hotelData.getLongitude());
        if (hotelData.getStarRating() != null) hotel.setStarRating(hotelData.getStarRating());
        if (hotelData.getPropertyType() != null) hotel.setPropertyType(hotelData.getPropertyType());
        if (hotelData.getCheckInTime() != null) hotel.setCheckInTime(hotelData.getCheckInTime());
        if (hotelData.getCheckOutTime() != null) hotel.setCheckOutTime(hotelData.getCheckOutTime());
        if (hotelData.getPolicies() != null) hotel.setPolicies(hotelData.getPolicies());
        if (hotelData.getContactPhone() != null) hotel.setContactPhone(hotelData.getContactPhone());
        if (hotelData.getContactEmail() != null) hotel.setContactEmail(hotelData.getContactEmail());
        
        return hotelRepository.save(hotel);
    }
    
    @Transactional
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }
    
    @Transactional
    public Hotel addAmenity(Long hotelId, String amenity) {
        Hotel hotel = getHotelById(hotelId);
        hotel.getAmenities().add(amenity);
        return hotelRepository.save(hotel);
    }
    
    @Transactional
    public Hotel addPhoto(Long hotelId, String photoUrl) {
        Hotel hotel = getHotelById(hotelId);
        hotel.getPhotos().add(photoUrl);
        return hotelRepository.save(hotel);
    }
}