package com.hotel.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.User;
import com.hotel.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    @Transactional
    public User updateProfile(User updateData) {
        User user = getCurrentUser();
        
        if (updateData.getFirstName() != null) user.setFirstName(updateData.getFirstName());
        if (updateData.getLastName() != null) user.setLastName(updateData.getLastName());
        if (updateData.getPhone() != null) user.setPhone(updateData.getPhone());
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void updatePassword(String oldPassword, String newPassword) {
        User user = getCurrentUser();
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    @Transactional
    public User addWalletBalance(Double amount) {
        User user = getCurrentUser();
        user.setWalletBalance(user.getWalletBalance() + amount);
        return userRepository.save(user);
    }
    
    @Transactional
    public User deductWalletBalance(Double amount) {
        User user = getCurrentUser();
        if (user.getWalletBalance() < amount) {
            throw new RuntimeException("Insufficient wallet balance");
        }
        user.setWalletBalance(user.getWalletBalance() - amount);
        return userRepository.save(user);
    }
}
