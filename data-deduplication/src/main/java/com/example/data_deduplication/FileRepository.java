package com.example.data_deduplication;

import java.util.List;

import org.eclipse.angus.mail.imap.protocol.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FileRepository extends JpaRepository<UserFile, Long> { // Must be Long
    
    List<UserFile> findByUserId(Long userId);
 // NEW: Find files shared WITH me by my email
    List<UserFile> findBySharedWithEmail(String email);
    boolean existsByFileHash(String fileHash);
    List<UserFile> findByUserIdNot(Long userId);
    List<UserFile> findByUserIdAndShareOtpIsNotNull(Long userId);
    
}