package com.hotel.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.model.PromoCode;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCode(String code);
    
    @Query("SELECT p FROM PromoCode p WHERE p.code = :code AND p.active = true " +
           "AND p.validFrom <= :now AND p.validUntil >= :now " +
           "AND (p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    Optional<PromoCode> findValidPromoCode(
        @Param("code") String code,
        @Param("now") LocalDateTime now
    );
    
    List<PromoCode> findByActiveTrue();
}