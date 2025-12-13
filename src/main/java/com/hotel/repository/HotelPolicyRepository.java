package com.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.model.HotelPolicy;

@Repository
public interface HotelPolicyRepository extends JpaRepository<HotelPolicy, Long> {
    
    /**
     * Find hotel policy by hotel ID
     */
    Optional<HotelPolicy> findByHotelId(Long hotelId);
    
    /**
     * Check if policy exists for hotel
     */
    boolean existsByHotelId(Long hotelId);
    
    /**
     * Delete policy by hotel ID
     */
    void deleteByHotelId(Long hotelId);
}