package com.example.data_deduplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private FileRepository fileRepository;
    @Autowired private DownloadLogRepository logRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalFiles", fileRepository.count());
        stats.put("totalLogs", logRepository.count());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/all-files")
    public ResponseEntity<List<Map<String, Object>>> getAllFiles() {
        List<UserFile> files = fileRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (UserFile file : files) {
            Map<String, Object> map = new HashMap<>();
            User owner = userRepository.findById(file.getUserId()).orElse(null);
            map.put("fileName", file.getFileName());
            map.put("fileType", file.getContentType());
            map.put("ownerName", owner != null ? owner.getName() : "Unknown");
            map.put("ownerEmail", owner != null ? owner.getEmail() : "Unknown");
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }
 // Line Chart Data: Files per Day
    @GetMapping("/upload-stats")
    public ResponseEntity<Map<String, Long>> getUploadStats() {
        List<UserFile> allFiles = fileRepository.findAll();
        
        // DEBUG: Print size to console
        System.out.println("Total files found: " + allFiles.size());

        Map<String, Long> stats = allFiles.stream()
            // If otp_created_at is NULL in DB, the chart will be blank!
            .filter(f -> f.getOtpCreatedAt() != null) 
            .collect(Collectors.groupingBy(
                f -> f.getOtpCreatedAt().toLocalDate().toString(),
                TreeMap::new,
                Collectors.counting()
            ));
        return ResponseEntity.ok(stats);
    }

    // Pie Chart Data: User vs Admin
    @GetMapping("/role-distribution")
    public ResponseEntity<Map<String, Long>> getRoleDistribution() {
        List<User> allUsers = userRepository.findAll();
        Map<String, Long> roleStats = allUsers.stream()
            .collect(Collectors.groupingBy(
                User::getRole,
                Collectors.counting()
            ));
        return ResponseEntity.ok(roleStats);
    }
    
    @GetMapping("/global-logs")
    public ResponseEntity<List<DownloadLog>> getGlobalLogs() {
        return ResponseEntity.ok(logRepository.findAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User and associated nodes removed"));
    }
}