package com.example.data_deduplication;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;



@Entity
@Table(name = "user_files")
@Data
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileHash; // SHA-256 Fingerprint
    private Long userId;
    private String contentType; // Store file type (e.g., image/png)
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data; // The actual file content
    
    private String shareOtp;       // The code sent via email
    private String sharedWithEmail; // Target user's email
    
    private LocalDateTime otpCreatedAt;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileHash() {
		return fileHash;
	}
	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getShareOtp() {
		return shareOtp;
	}
	public void setShareOtp(String shareOtp) {
		this.shareOtp = shareOtp;
	}
	public String getSharedWithEmail() {
		return sharedWithEmail;
	}
	public void setSharedWithEmail(String sharedWithEmail) {
		this.sharedWithEmail = sharedWithEmail;
	}
	public LocalDateTime getOtpCreatedAt() {
		return otpCreatedAt;
	}
	public void setOtpCreatedAt(LocalDateTime otpCreatedAt) {
		this.otpCreatedAt = otpCreatedAt;
	}
    
}