package com.hotel.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.Booking;
import com.hotel.model.SavedCard;
import com.hotel.model.User;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.SavedCardRepository;
import com.hotel.service.BookingService;
import com.hotel.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final SavedCardRepository savedCardRepository;

    // ================= PROCESS PAYMENT =================
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> request) {
        try {
            Long bookingId = ((Number) request.get("bookingId")).longValue();
            String paymentMethod = (String) request.get("paymentMethod");

            Booking booking = bookingService.getBookingById(bookingId);

            String transactionId = "TXN" + System.currentTimeMillis();
            Map<String, Object> response;

            switch (paymentMethod.toUpperCase()) {
                case "UPI":
                    response = processUpiPayment(
                            booking,
                            (String) request.get("upiId"),
                            transactionId
                    );
                    break;

                case "CARD":
                case "CREDIT_CARD":
                case "DEBIT_CARD":
                    response = processCardPayment(booking, request, transactionId);
                    break;

                case "EMI":
                    response = processEmiPayment(
                            booking,
                            request,
                            ((Number) request.get("emiTenure")).intValue(),
                            transactionId
                    );
                    break;

                default:
                    throw new RuntimeException("Invalid payment method");
            }

            booking.setPaymentMethod(paymentMethod);
            booking.setPaymentStatus(Booking.PaymentStatus.PAID);
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            response.put("success", true);
            response.put("bookingId", bookingId);
            response.put("bookingStatus", "CONFIRMED");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ================= SAVE CARD =================
    @PostMapping("/save-card")
    public ResponseEntity<?> saveCard(@RequestBody Map<String, String> cardData) {
        try {
            User currentUser = userService.getCurrentUser();

            String cardNumber = cardData.get("cardNumber");
            String last4 = cardNumber.substring(cardNumber.length() - 4);

            SavedCard savedCard = new SavedCard();
            savedCard.setUser(currentUser);
            savedCard.setCardNumberEncrypted("ENCRYPTED_" + cardNumber.hashCode());
            savedCard.setCardHolderName(cardData.get("cardHolderName"));
            savedCard.setCardType(cardData.getOrDefault("cardType", "UNKNOWN"));
            savedCard.setExpiryMonth(cardData.get("expiryMonth"));
            savedCard.setExpiryYear(cardData.get("expiryYear"));
            savedCard.setDefaultCard(false);

            savedCardRepository.save(savedCard);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Card saved successfully",
                    "cardLast4", last4
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ================= GET SAVED CARDS =================
    @GetMapping("/saved-cards")
    public ResponseEntity<?> getSavedCards() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "cards", savedCardRepository.findByUserId(currentUser.getId())
        ));
    }

    // ================= PAYMENT METHODS =================
    @GetMapping("/methods")
    public ResponseEntity<?> getPaymentMethods() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "paymentMethods", Map.of(
                        "UPI", "UPI / QR Code",
                        "CARD", "Credit / Debit Card",
                        "EMI", "Easy EMI"
                )
        ));
    }

    // ================= HELPER METHODS =================

    private Map<String, Object> processUpiPayment(Booking booking, String upiId, String txnId) {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionId", txnId);
        map.put("paymentMethod", "UPI");
        map.put("upiId", upiId);
        map.put("amount", booking.getFinalPrice());
        map.put("message", "Payment successful via UPI");
        map.put("timestamp", LocalDateTime.now().toString());
        return map;
    }

    private Map<String, Object> processCardPayment(Booking booking, Map<String, Object> request, String txnId) {
        String cardNumber = (String) request.get("cardNumber");
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String cardType = getCardType(cardNumber);

        Boolean saveCard = (Boolean) request.getOrDefault("saveCard", false);
        if (saveCard) {
            User user = userService.getCurrentUser();

            SavedCard card = new SavedCard();
            card.setUser(user);
            card.setCardNumberEncrypted("ENCRYPTED_" + cardNumber.hashCode());
            card.setCardHolderName((String) request.get("cardHolderName"));
            card.setCardType(cardType);
            card.setExpiryMonth((String) request.get("expiryMonth"));
            card.setExpiryYear((String) request.get("expiryYear"));
            card.setDefaultCard(false);

            savedCardRepository.save(card);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("transactionId", txnId);
        map.put("paymentMethod", "CARD");
        map.put("cardType", cardType);
        map.put("cardLast4", last4);
        map.put("amount", booking.getFinalPrice());
        map.put("message", "Payment successful");
        map.put("timestamp", LocalDateTime.now().toString());
        return map;
    }

    private Map<String, Object> processEmiPayment(
            Booking booking,
            Map<String, Object> request,
            Integer tenure,
            String txnId
    ) {
        double amount = booking.getFinalPrice();
        double rate = 12.0 / 12 / 100;

        double emi = amount * rate * Math.pow(1 + rate, tenure)
                / (Math.pow(1 + rate, tenure) - 1);

        Map<String, Object> map = new HashMap<>();
        map.put("transactionId", txnId);
        map.put("paymentMethod", "EMI");
        map.put("monthlyEmi", String.format("%.2f", emi));
        map.put("tenure", tenure + " months");
        map.put("totalPayable", String.format("%.2f", emi * tenure));
        map.put("timestamp", LocalDateTime.now().toString());
        return map;
    }

    private String getCardType(String cardNumber) {
        if (cardNumber.startsWith("4")) return "VISA";
        if (cardNumber.startsWith("5")) return "MASTERCARD";
        if (cardNumber.startsWith("6")) return "RUPAY";
        if (cardNumber.startsWith("3")) return "AMEX";
        return "UNKNOWN";
    }
}
