package com.example.data_deduplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Handles complete user registration: 
     * 1. Saves to DB 
     * 2. Triggers Welcome Email in background
     */
    public User registerNewUser(User user) {
        // Step 1: Save the user to the database
        // (ID will be auto-generated here)
        User savedUser = userRepository.save(user);
        
        // Step 2: Send Welcome Email ASYNCHRONOUSLY
        // This prevents the UI from "freezing" while waiting for the email server
        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName());
            } catch (Exception e) {
                System.err.println("Background Email Task Failed: " + e.getMessage());
            }
        });

        return savedUser;
    }

    /**
     * Logic for finding user by email
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Logic for updating profile picture
     */
    public User updateProfilePic(Long id, String base64Pic) {
        return userRepository.findById(id).map(user -> {
            user.setProfilePic(base64Pic);
            return userRepository.save(user);
        }).orElse(null);
    }
}