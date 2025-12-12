package com.hotel.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.Booking;
import com.hotel.model.Hotel;
import com.hotel.model.Review;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.HotelRepository;
import com.hotel.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    
    @Transactional
    public Review submitReview(Review review) {
        var currentUser = userService.getCurrentUser();
        review.setUser(currentUser);
        
        if (review.getBooking() != null) {
            Booking booking = bookingRepository.findById(review.getBooking().getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            if (!booking.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Unauthorized to review this booking");
            }
            
            if (reviewRepository.existsByUserIdAndBookingId(currentUser.getId(), booking.getId())) {
                throw new RuntimeException("You have already reviewed this booking");
            }
            
            review.setVerified(true);
        }
        
        Review savedReview = reviewRepository.save(review);
        
        updateHotelRating(review.getHotel().getId());
        
        return savedReview;
    }
    
    public List<Review> getHotelReviews(Long hotelId) {
        return reviewRepository.findByHotelIdOrderByCreatedAtDesc(hotelId);
    }
    
    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
    
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        var currentUser = userService.getCurrentUser();
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to delete this review");
        }
        
        Long hotelId = review.getHotel().getId();
        reviewRepository.delete(review);
        
        updateHotelRating(hotelId);
    }
    
    private void updateHotelRating(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        Double avgRating = reviewRepository.calculateAverageRating(hotelId);
        Long reviewCount = reviewRepository.countReviewsByHotelId(hotelId);
        
        hotel.setAverageRating(avgRating != null ? avgRating : 0.0);
        hotel.setTotalReviews(reviewCount.intValue());
        
        hotelRepository.save(hotel);
    }
}