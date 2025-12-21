package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.PromoCode;
import com.hotel.service.PromoCodeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PromoCodeController {
    
    private final PromoCodeService promoCodeService;
    
    @PostMapping("/validate")
public ResponseEntity<?> validatePromoCode(@RequestBody Map<String, Object> request) {

    String code = (String) request.get("code");
    Double amount = ((Number) request.get("bookingAmount")).doubleValue();

    PromoCode promoCode = promoCodeService.validatePromoCode(code, amount);
    Double discount = promoCodeService.calculateDiscount(promoCode, amount);

    return ResponseEntity.ok(Map.of(
        "valid", true,
        "code", promoCode.getCode(),
        "discountType", promoCode.getDiscountType().name(),
        "discountValue", promoCode.getDiscountValue(),
        "discountAmount", discount,
        "message", "Promo code applied successfully"
    ));
}

    
    @GetMapping("/active")
    public ResponseEntity<List<PromoCode>> getActivePromoCodes() {
        return ResponseEntity.ok(promoCodeService.getActivePromoCodes());
    }
    
    @PostMapping
    public ResponseEntity<PromoCode> createPromoCode(@RequestBody PromoCode promoCode) {
        return ResponseEntity.ok(promoCodeService.createPromoCode(promoCode));
    }
}