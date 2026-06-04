package com.example.data_deduplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to CloudPortal!");
        message.setText("Hello " + name + ",\n\nWelcome to our platform! Your account is now active.\n\nRegards,\nTeam CloudPortal");
        
        mailSender.send(message);
    }
    
    public void sendSupportRequest(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Support Request Received");
        message.setText("We have received your request for support. Our team will contact you within 24 hours.");
        
        mailSender.send(message);
    }
    
    public void sendOtpEmail(String to, String fileName, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("SECURE FILE ACCESS: " + fileName);
        message.setText("Someone shared a secure file with you.\n\n" +
                       "File: " + fileName + "\n" +
                       "Your Access OTP: " + otp + "\n\n" +
                       "This code is for one-time use only.");
        mailSender.send(message);
    }
    
    public void sendRequestAlertEmail(String ownerEmail, String fileName, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ownerEmail);
        message.setSubject("File Access Request: " + fileName);
        message.setText("Another user has requested access to your file: " + fileName + 
                       "\n\nIf you wish to grant them access, provide them with this OTP: " + otp + 
                       "\n\nThis code expires in 10 minutes.");
        mailSender.send(message);
    }
    
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("VaultPro | Password Reset Code");
        message.setText("Your security verification code is: " + otp + 
                        "\n\nThis code will expire in 5 minutes.");
        mailSender.send(message);
    }
}