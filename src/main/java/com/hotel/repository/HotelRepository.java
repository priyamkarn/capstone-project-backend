package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.model.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCityIgnoreCase(String city);
    List<Hotel> findByPinCode(String pinCode);
    List<Hotel> findByLandmarkContainingIgnoreCase(String landmark);
    List<Hotel> findByStarRating(Integer starRating);
    List<Hotel> findByPropertyType(Hotel.PropertyType propertyType);

    @Query("SELECT h FROM Hotel h WHERE h.averageRating >= :minRating")
    List<Hotel> findByMinimumRating(@Param("minRating") Double minRating);

    // ⭐ SORT BY RATING
    List<Hotel> findAllByOrderByAverageRatingAsc();
    List<Hotel> findAllByOrderByAverageRatingDesc();

    // 💰 SORT BY PRICE (MIN ROOM PRICE)
    @Query("""
        SELECT DISTINCT h FROM Hotel h
        LEFT JOIN h.rooms r
        GROUP BY h
        ORDER BY MIN(r.pricePerNight) ASC
    """)
    List<Hotel> sortByPriceAsc();

    @Query("""
        SELECT DISTINCT h FROM Hotel h
        LEFT JOIN h.rooms r
        GROUP BY h
        ORDER BY MIN(r.pricePerNight) DESC
    """)
    List<Hotel> sortByPriceDesc();

    @Query("""
        SELECT h FROM Hotel h WHERE
        LOWER(h.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(h.city) LIKE LOWER(CONCAT('%', :search, '%')) OR
        LOWER(h.landmark) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    List<Hotel> searchHotels(@Param("search") String search);
}
