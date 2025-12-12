package com.hotel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.Booking;
import com.hotel.model.BookingExtra;
import com.hotel.model.BookingRoom;
import com.hotel.model.Invoice;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.BookingRoomRepository;
import com.hotel.repository.HotelRepository;
import com.hotel.repository.InvoiceRepository;
import com.hotel.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingRoomRepository bookingRoomRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final UserService userService;
    private final InvoiceRepository invoiceRepository;

    @Transactional
    public Booking createBooking(Booking booking, List<BookingRoom> rooms) {

    User currentUser = userService.getCurrentUser();
    booking.setUser(currentUser);
    booking.setBookingReference(generateBookingReference());
    booking.setBookingNumber(generateBookingReference());
    booking.setStatus(Booking.BookingStatus.PENDING);
    booking.setPaymentStatus(Booking.PaymentStatus.PENDING);

        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        double totalPrice = 0.0;

        for (BookingRoom room : rooms) {
            Room roomEntity = roomRepository.findById(room.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            Integer bookedCount = bookingRoomRepository.countBookedRooms(
                    roomEntity.getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate()
            );

            if (roomEntity.getTotalRooms() - bookedCount < room.getNumberOfRooms()) {
                throw new RuntimeException("Not enough rooms available");
            }

            room.setPricePerNight(roomEntity.getPricePerNight());
            totalPrice += roomEntity.getPricePerNight() * room.getNumberOfRooms() * nights;
        }

        if (booking.getExtras() != null) {
            totalPrice += booking.getExtras().stream()
                    .mapToDouble(BookingExtra::getPrice)
                    .sum();
        }

        booking.setTotalPrice(totalPrice);
        double taxAmount = totalPrice * 0.12;
        booking.setTaxAmount(taxAmount);

        double discount = Optional.ofNullable(booking.getDiscountAmount()).orElse(0.0);
        double finalPrice = totalPrice + taxAmount - discount;
        booking.setFinalPrice(finalPrice);


        booking.setFinalPrice(finalPrice);

        Booking savedBooking = bookingRepository.save(booking);

        for (BookingRoom room : rooms) {
            room.setBooking(savedBooking);
            bookingRoomRepository.save(room);
        }

        return savedBooking;
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    public Booking getBookingByReference(String reference) {
        return bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new RuntimeException("Booking not found with reference: " + reference));
    }

    public List<Booking> getCurrentUserBookings() {
        User user = userService.getCurrentUser();
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<Booking> getHotelBookings(Long hotelId) {
        return bookingRepository.findByHotelId(hotelId);
    }

    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentStatus(Booking.PaymentStatus.PAID);

        generateInvoice(booking);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking already cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());

        if (booking.getPaymentStatus() == Booking.PaymentStatus.PAID) {
            booking.setPaymentStatus(Booking.PaymentStatus.REFUNDED);
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking modifyBooking(Long bookingId, LocalDate newCheckIn, LocalDate newCheckOut) {
        Booking booking = getBookingById(bookingId);

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot modify cancelled booking");
        }

        booking.setCheckInDate(newCheckIn);
        booking.setCheckOutDate(newCheckOut);
        booking.setModifiedAt(LocalDateTime.now());

        long nights = ChronoUnit.DAYS.between(newCheckIn, newCheckOut);

        double newTotal = booking.getBookingRooms().stream()
                .mapToDouble(br -> br.getPricePerNight() * br.getNumberOfRooms() * nights)
                .sum();

        booking.setTotalPrice(newTotal);
        booking.setTaxAmount(newTotal * 0.12);
        booking.setFinalPrice(newTotal + booking.getTaxAmount() - booking.getDiscountAmount());

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking checkIn(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(Booking.BookingStatus.CHECKED_IN);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking checkOut(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(Booking.BookingStatus.CHECKED_OUT);
        return bookingRepository.save(booking);
    }

    private String generateBookingReference() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void generateInvoice(Booking booking) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setGeneratedAt(LocalDateTime.now());
        invoice.setBooking(booking);

        invoiceRepository.save(invoice);
    }
}
