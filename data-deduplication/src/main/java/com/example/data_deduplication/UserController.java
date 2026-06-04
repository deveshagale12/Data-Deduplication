package com.example.data_deduplication;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;


import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") 

public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileRepository fileRepository;
    
    @Autowired 
    private DownloadLogRepository logRepository;

    @Autowired
    private JavaMailSender mailSender;

    // --- 1. SECURE UPLOAD (SHA-256 Deduplication) ---
    @PostMapping("/upload-secure")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
        try {
            byte[] fileBytes = file.getBytes();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);
            String fileHash = HexFormat.of().formatHex(hashBytes);

            if (fileRepository.existsByFileHash(fileHash)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Duplicate Content: This file already exists in the vault.");
            }

            UserFile userFile = new UserFile();
            userFile.setFileName(file.getOriginalFilename());
            userFile.setFileHash(fileHash);
            userFile.setUserId(userId);
            userFile.setData(fileBytes); 
            userFile.setContentType(file.getContentType());
            
            fileRepository.save(userFile);
            return ResponseEntity.ok("File secured successfully.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }  // --- 2. AUTHENTICATION (FIXED FOR ALL FIELDS & ROLE) ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> payload) {
        try {
            User user = new User();
            // Core Identity
            user.setName(payload.get("name"));
            user.setEmail(payload.get("email"));
            user.setPassword(payload.get("password"));
            user.setProfilePic(payload.get("profilePic"));
            
            // --- FIXED: ADDING MISSING FIELDS ---
            user.setMobile(payload.get("mobile"));
            user.setEducation(payload.get("education"));
            user.setAddress(payload.get("address"));
            
            // Handle Date of Birth (String to LocalDate)
            if (payload.get("dob") != null && !payload.get("dob").isEmpty()) {
                user.setDob(LocalDate.parse(payload.get("dob")));
            }

            // --- FIXED: ROLE LOGIC ---
            // If the frontend sends a role (like ADMIN), we use it. 
            // Otherwise, we check for the secret adminKey as a backup.
            String selectedRole = payload.get("role");
            String adminKey = payload.get("adminKey");

            if ("ADMIN".equalsIgnoreCase(selectedRole) || "VAULT_2026".equals(adminKey)) {
                user.setRole("ADMIN");
            } else {
                user.setRole("USER");
            }

            User savedUser = userRepository.save(user);
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName());
            
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        try {
            User user = userRepository.findByEmail(credentials.get("email"));
            if (user != null && user.getPassword().equals(credentials.get("password"))) {
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (IncorrectResultSizeDataAccessException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body("Security Alert: Duplicate identity detected. Contact Admin.");
        }
    } // --- 3. PROFILE & FILE MANAGEMENT ---
 // 1. Update Profile Text (Name)
 // Fixes: POST http://localhost:8080/api/users/update-profile
 @PostMapping("/update-profile")
 public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> data) {
     try {
         // We use "userId" because your JS sends: body: JSON.stringify({ userId: user.id, ... })
         Long id = Long.valueOf(data.get("userId").toString());
         String newName = (String) data.get("name");

         return userRepository.findById(id).map(user -> {
             user.setName(newName);
             userRepository.save(user);
             return ResponseEntity.ok(user);
         }).orElse(ResponseEntity.notFound().build());
     } catch (Exception e) {
         return ResponseEntity.badRequest().body("Error: " + e.getMessage());
     }
 }

 // 2. Update Profile Picture (Base64)
 // Fixes: POST http://localhost:8080/api/users/update-profile-pic
 @PostMapping("/update-profile-pic")
 public ResponseEntity<?> updateProfilePic(@RequestBody Map<String, Object> data) {
     try {
         Long id = Long.valueOf(data.get("userId").toString());
         String base64Pic = (String) data.get("profilePic");

         return userRepository.findById(id).map(user -> {
             user.setProfilePic(base64Pic);
             userRepository.save(user);
             return ResponseEntity.ok(user);
         }).orElse(ResponseEntity.notFound().build());
     } catch (Exception e) {
         return ResponseEntity.badRequest().body("Error: " + e.getMessage());
     }
 }

    @GetMapping("/my-files/{userId}")
    public ResponseEntity<List<UserFile>> getFiles(@PathVariable Long userId) {
        return ResponseEntity.ok(fileRepository.findByUserId(userId));
    }

    @GetMapping("/download-file/{fileId}")
    public ResponseEntity<byte[]> download(@PathVariable Long fileId) {
        Optional<UserFile> fileOptional = fileRepository.findById(fileId);
        if (fileOptional.isPresent()) {
            UserFile file = fileOptional.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .body(file.getData());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).<byte[]>build();
        }
    }

    @GetMapping("/community-files/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getCommunity(@PathVariable Long userId) {
        List<UserFile> files = fileRepository.findByUserIdNot(userId);
        List<Map<String, Object>> response = new ArrayList<>();
        for (UserFile file : files) {
            Map<String, Object> map = new HashMap<>();
            User owner = userRepository.findById(file.getUserId()).orElse(null);
            map.put("id", file.getId());
            map.put("fileName", file.getFileName());
            map.put("fileHash", file.getFileHash());
            map.put("ownerName", owner != null ? owner.getName() : "Unknown User");
            map.put("ownerEmail", owner != null ? owner.getEmail() : "No Email");
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return ResponseEntity.badRequest().body("Email not found");
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        emailService.sendOtpEmail(email, otp);
        return ResponseEntity.ok("OTP Sent Successfully");
    }

    @PostMapping("/otp/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String otp = data.get("otp");
        String newPassword = data.get("newPassword");

        if (email == null || otp == null || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields required");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        if (user.getOtp() == null || !user.getOtp().equals(otp)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) return ResponseEntity.status(HttpStatus.GONE).body("OTP Expired");

        user.setPassword(newPassword);
        user.setOtp(null); 
        user.setOtpExpiry(null);
        userRepository.save(user);
        return ResponseEntity.ok("Password updated successfully.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmail(email);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");

        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("your-email@gmail.com");
            helper.setTo(email);
            helper.setSubject("VaultPro | Security Verification Code");
            String htmlMsg = "<div style='font-family: Arial; padding: 20px;'><h1>OTP: " + otp + "</h1></div>";
            helper.setText(htmlMsg, true);
            mailSender.send(message);
            return ResponseEntity.ok("OTP sent to email.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/request-access")
    public ResponseEntity<?> requestAccess(@RequestBody Map<String, Object> data) {
        Long fileId = Long.valueOf(data.get("fileId").toString());
        String requesterEmail = data.get("requesterEmail").toString();
        return fileRepository.findById(fileId).<ResponseEntity<?>>map(file -> {
            User owner = userRepository.findById(file.getUserId()).orElse(null);
            if(owner == null) return ResponseEntity.notFound().build();
            String otp = String.valueOf(new Random().nextInt(899999) + 100000);
            file.setShareOtp(otp);
            file.setSharedWithEmail(requesterEmail); 
            file.setOtpCreatedAt(LocalDateTime.now());
            fileRepository.save(file);
            emailService.sendRequestAlertEmail(owner.getEmail(), file.getFileName(), otp);
            return ResponseEntity.ok("Request sent!");
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending-requests/{userId}")
    public ResponseEntity<List<UserFile>> getPendingRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(fileRepository.findByUserIdAndShareOtpIsNotNull(userId));
    }

    @PostMapping("/revoke-access/{fileId}")
    public ResponseEntity<?> revokeAccess(@PathVariable Long fileId) {
        return fileRepository.findById(fileId).map(file -> {
            file.setShareOtp(null);
            file.setSharedWithEmail(null);
            fileRepository.save(file);
            return ResponseEntity.ok("Access revoked.");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/share-file")
    public ResponseEntity<?> shareFile(@RequestBody Map<String, String> data) {
        String targetEmail = data.get("email");
        Long fileId = Long.valueOf(data.get("fileId").toString());
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        return fileRepository.findById(fileId).<ResponseEntity<?>>map(file -> {
            file.setShareOtp(otp);
            file.setSharedWithEmail(targetEmail);
            file.setOtpCreatedAt(LocalDateTime.now());
            fileRepository.save(file);
            emailService.sendOtpEmail(targetEmail, file.getFileName(), otp);
            return ResponseEntity.ok("OTP sent to " + targetEmail);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/verify-and-download/{fileId}")
    public ResponseEntity<byte[]> verify(@PathVariable Long fileId, @RequestBody Map<String, String> data) {
        String userOtp = data.get("otp");
        String downloaderEmail = data.get("email");
        Optional<UserFile> fileOpt = fileRepository.findById(fileId);
        if (fileOpt.isPresent() && fileOpt.get().getShareOtp().equals(userOtp)) {
            UserFile file = fileOpt.get();
            DownloadLog log = new DownloadLog();
            log.setFileId(fileId);
            log.setFileName(file.getFileName());
            log.setDownloaderEmail(downloaderEmail);
            log.setDownloadTime(LocalDateTime.now());
            logRepository.save(log);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getData());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/audit-logs/{userId}")
    public ResponseEntity<List<DownloadLog>> getLogs(@PathVariable Long userId) {
        return ResponseEntity.ok(logRepository.findAllByOwnerId(userId)); 
    }

    @DeleteMapping("/delete-file/{fileId}")
    public ResponseEntity<?> delete(@PathVariable Long fileId) {
        if (fileRepository.existsById(fileId)) {
            fileRepository.deleteById(fileId);
            return ResponseEntity.ok(Map.of("status", "deleted"));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/shared-with-me/{email}")
    public ResponseEntity<List<UserFile>> getSharedWithMe(@PathVariable String email) {
        return ResponseEntity.ok(fileRepository.findBySharedWithEmail(email));
    }
}