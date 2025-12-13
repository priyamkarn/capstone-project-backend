package com.hotel.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    // REGISTER USER
    public Map<String, Object> register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // encode password and set default role
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);
        user.setEnabled(true);
        
        User savedUser = userRepository.save(user);
        
        // set authorities for JWT
        var authorities = java.util.List.of(
            new SimpleGrantedAuthority("ROLE_" + savedUser.getRole().name())
        );
        
        // generate JWT token
        var jwtToken = jwtService.generateToken(
            new org.springframework.security.core.userdetails.User(
                savedUser.getEmail(),
                savedUser.getPassword(),
                authorities
            )
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("user", savedUser);
        return response;
    }
    
    // LOGIN USER
    public Map<String, Object> login(String email, String password) {
        // authenticate user credentials
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        var authorities = java.util.List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        
        var jwtToken = jwtService.generateToken(
            new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
            )
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);
        //response.put("user", user);
        return response;
    }
}
