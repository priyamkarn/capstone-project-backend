package com.hotel.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceController {
    
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getInvoice(@PathVariable Long bookingId) {
        return ResponseEntity.ok(Map.of(
            "invoiceUrl", "/invoices/" + bookingId + ".pdf",
            "message", "Invoice generated successfully"
        ));
    }
    
    @GetMapping("/booking/{bookingId}/download")
    public ResponseEntity<?> downloadInvoice(@PathVariable Long bookingId) {
        return ResponseEntity.ok(Map.of("message", "Invoice download initiated"));
    }
    
    @GetMapping("/booking/{bookingId}/voucher")
    public ResponseEntity<?> getVoucher(@PathVariable Long bookingId) {
        return ResponseEntity.ok(Map.of(
            "voucherUrl", "/vouchers/" + bookingId + ".pdf",
            "message", "Voucher generated successfully"
        ));
    }
}
