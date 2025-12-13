package com.hotel.model;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hotel_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnore

    private Hotel hotel;
    
    // Check-in/Check-out Policies
    private LocalTime checkInStartTime;
    private LocalTime checkInEndTime;
    private LocalTime checkOutTime;
    private Boolean earlyCheckInAvailable = false;
    private Double earlyCheckInFee = 0.0;
    private Boolean lateCheckOutAvailable = false;
    private Double lateCheckOutFee = 0.0;
    
    // Age Policies
    private Integer minimumCheckInAge = 18;
    private Boolean childrenAllowed = true;
    private Boolean petsAllowed = false;
    private Double petFee = 0.0;
    
    // Payment Policies
    @Enumerated(EnumType.STRING)
    private PaymentTiming paymentTiming = PaymentTiming.AT_HOTEL;
    
    private Boolean depositRequired = false;
    private Double depositAmount = 0.0;
    
    // Smoking Policy
    private Boolean smokingAllowed = false;
    private String smokingPolicy;
    
    // Party/Event Policy
    private Boolean partiesAllowed = false;
    
    // Additional Policies
    @Column(length = 2000)
    private String additionalPolicies;
    
    @Column(length = 2000)
    private String covidPolicies;
    
    @Column(length = 1000)
    private String houseRules;
    
    public enum PaymentTiming {
        AT_HOTEL, ADVANCE_PAYMENT, PARTIAL_ADVANCE
    }
}