package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.model.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByCity(String city);
    List<Hotel> findByCityIgnoreCase(String city);
    List<Hotel> findByPinCode(String pinCode);
    List<Hotel> findByLandmarkContainingIgnoreCase(String landmark);
    
    @Query("SELECT h FROM Hotel h WHERE " +
           "LOWER(h.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(h.landmark) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(h.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Hotel> searchHotels(@Param("search") String search);
    
    List<Hotel> findByStarRating(Integer starRating);
    List<Hotel> findByPropertyType(Hotel.PropertyType propertyType);
    List<Hotel> findByStarRatingGreaterThanEqual(Integer minRating);
    
    @Query("SELECT h FROM Hotel h WHERE h.averageRating >= :minRating")
    List<Hotel> findByMinimumRating(@Param("minRating") Double minRating);
}