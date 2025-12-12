package com.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.model.HotelComparison;

@Repository
public interface HotelComparisonRepository extends JpaRepository<HotelComparison, Long> {
    List<HotelComparison> findByUserId(Long userId);
    Optional<HotelComparison> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}