package com.hotel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.Hotel;
import com.hotel.model.HotelPolicy;
import com.hotel.repository.HotelPolicyRepository;
import com.hotel.repository.HotelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class HotelPolicyService {
    
    private final HotelPolicyRepository hotelPolicyRepository;
    private final HotelRepository hotelRepository;
    
    /**
     * Get hotel policy by hotel ID
     */
    public HotelPolicy getHotelPolicy(Long hotelId) {
        return hotelPolicyRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel policy not found for hotel ID: " + hotelId));
    }
    
    /**
     * Create or update hotel policy
     */
    public HotelPolicy createOrUpdatePolicy(Long hotelId, HotelPolicy policyData) {
        // Fetch the hotel
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
        
        // Check if policy already exists for this hotel
        HotelPolicy policy = hotelPolicyRepository.findByHotelId(hotelId)
                .orElse(new HotelPolicy());
        
        // Set hotel reference
        policy.setHotel(hotel);
        
        // Update check-in/check-out policies
        if (policyData.getCheckInStartTime() != null) {
            policy.setCheckInStartTime(policyData.getCheckInStartTime());
        }
        if (policyData.getCheckInEndTime() != null) {
            policy.setCheckInEndTime(policyData.getCheckInEndTime());
        }
        if (policyData.getCheckOutTime() != null) {
            policy.setCheckOutTime(policyData.getCheckOutTime());
        }
        if (policyData.getEarlyCheckInAvailable() != null) {
            policy.setEarlyCheckInAvailable(policyData.getEarlyCheckInAvailable());
        }
        if (policyData.getEarlyCheckInFee() != null) {
            policy.setEarlyCheckInFee(policyData.getEarlyCheckInFee());
        }
        if (policyData.getLateCheckOutAvailable() != null) {
            policy.setLateCheckOutAvailable(policyData.getLateCheckOutAvailable());
        }
        if (policyData.getLateCheckOutFee() != null) {
            policy.setLateCheckOutFee(policyData.getLateCheckOutFee());
        }
        
        // Update age policies
        if (policyData.getMinimumCheckInAge() != null) {
            policy.setMinimumCheckInAge(policyData.getMinimumCheckInAge());
        }
        if (policyData.getChildrenAllowed() != null) {
            policy.setChildrenAllowed(policyData.getChildrenAllowed());
        }
        if (policyData.getPetsAllowed() != null) {
            policy.setPetsAllowed(policyData.getPetsAllowed());
        }
        if (policyData.getPetFee() != null) {
            policy.setPetFee(policyData.getPetFee());
        }
        
        // Update payment policies
        if (policyData.getPaymentTiming() != null) {
            policy.setPaymentTiming(policyData.getPaymentTiming());
        }
        if (policyData.getDepositRequired() != null) {
            policy.setDepositRequired(policyData.getDepositRequired());
        }
        if (policyData.getDepositAmount() != null) {
            policy.setDepositAmount(policyData.getDepositAmount());
        }
        
        // Update smoking policy
        if (policyData.getSmokingAllowed() != null) {
            policy.setSmokingAllowed(policyData.getSmokingAllowed());
        }
        if (policyData.getSmokingPolicy() != null) {
            policy.setSmokingPolicy(policyData.getSmokingPolicy());
        }
        
        // Update party/event policy
        if (policyData.getPartiesAllowed() != null) {
            policy.setPartiesAllowed(policyData.getPartiesAllowed());
        }
        
        // Update additional policies
        if (policyData.getAdditionalPolicies() != null) {
            policy.setAdditionalPolicies(policyData.getAdditionalPolicies());
        }
        if (policyData.getCovidPolicies() != null) {
            policy.setCovidPolicies(policyData.getCovidPolicies());
        }
        if (policyData.getHouseRules() != null) {
            policy.setHouseRules(policyData.getHouseRules());
        }
        
        return hotelPolicyRepository.save(policy);
    }
    
    /**
     * Check if hotel has policy
     */
    public boolean hasPolicy(Long hotelId) {
        return hotelPolicyRepository.findByHotelId(hotelId).isPresent();
    }
    
    /**
     * Delete hotel policy
     */
    public void deletePolicy(Long hotelId) {
        HotelPolicy policy = hotelPolicyRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel policy not found for hotel ID: " + hotelId));
        hotelPolicyRepository.delete(policy);
    }
    
    /**
     * Get policy by ID
     */
    public HotelPolicy getPolicyById(Long id) {
        return hotelPolicyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel policy not found with ID: " + id));
    }
}