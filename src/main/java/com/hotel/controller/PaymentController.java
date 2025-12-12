package com.hotel.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> request) {
        // Payment processing logic would go here
        // Integration with payment gateway
        
        String paymentMethod = (String) request.get("paymentMethod");
        Double amount = ((Number) request.get("amount")).doubleValue();
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "transactionId", "TXN" + System.currentTimeMillis(),
            "message", "Payment processed successfully"
        ));
    }
    
    @PostMapping("/save-card")
    public ResponseEntity<?> saveCard(@RequestBody Map<String, String> cardData) {
        // Save card logic here
        return ResponseEntity.ok(Map.of("message", "Card saved successfully"));
    }
}