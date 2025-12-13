package com.hotel.controller;

import com.hotel.model.HotelPolicy;
import com.hotel.service.HotelPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel-policies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelPolicyController {
    
    private final HotelPolicyService hotelPolicyService;
    
    /**
     * Get hotel policy by hotel ID
     * GET /api/hotel-policies/hotel/1
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> getHotelPolicy(@PathVariable Long hotelId) {
        try {
            HotelPolicy policy = hotelPolicyService.getHotelPolicy(hotelId);
            return ResponseEntity.ok(policy);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Create or update hotel policy
     * POST /api/hotel-policies/hotel/1
     */
    @PostMapping("/hotel/{hotelId}")
    public ResponseEntity<?> createOrUpdatePolicy(
            @PathVariable Long hotelId,
            @RequestBody HotelPolicy policy) {
        try {
            HotelPolicy savedPolicy = hotelPolicyService.createOrUpdatePolicy(hotelId, policy);
            return ResponseEntity.ok(savedPolicy);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Check if hotel has policy
     * GET /api/hotel-policies/hotel/1/exists
     */
    @GetMapping("/hotel/{hotelId}/exists")
    public ResponseEntity<Map<String, Boolean>> hasPolicy(@PathVariable Long hotelId) {
        boolean exists = hotelPolicyService.hasPolicy(hotelId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}