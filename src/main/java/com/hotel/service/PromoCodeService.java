package com.hotel.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.PromoCode;
import com.hotel.repository.PromoCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromoCodeService {
    
    private final PromoCodeRepository promoCodeRepository;
    
    public PromoCode validatePromoCode(String code, Double bookingAmount) {
        PromoCode promoCode = promoCodeRepository.findValidPromoCode(code, LocalDateTime.now())
            .orElseThrow(() -> new RuntimeException("Invalid or expired promo code"));
        
        if (promoCode.getMinimumBookingAmount() != null && 
            bookingAmount < promoCode.getMinimumBookingAmount()) {
            throw new RuntimeException("Booking amount does not meet minimum requirement");
        }
        
        return promoCode;
    }
    
    public Double calculateDiscount(PromoCode promoCode, Double bookingAmount) {
        Double discount;
        
        if (promoCode.getDiscountType() == PromoCode.DiscountType.PERCENTAGE) {
            discount = bookingAmount * (promoCode.getDiscountValue() / 100);
        } else {
            discount = promoCode.getDiscountValue();
        }
        
        if (promoCode.getMaximumDiscountAmount() != null) {
            discount = Math.min(discount, promoCode.getMaximumDiscountAmount());
        }
        
        return discount;
    }
    
    @Transactional
    public void incrementUsage(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Promo code not found"));
        
        promoCode.setUsedCount(promoCode.getUsedCount() + 1);
        promoCodeRepository.save(promoCode);
    }
    
    public List<PromoCode> getActivePromoCodes() {
        return promoCodeRepository.findByActiveTrue();
    }
    
    @Transactional
    public PromoCode createPromoCode(PromoCode promoCode) {
        return promoCodeRepository.save(promoCode);
    }
}