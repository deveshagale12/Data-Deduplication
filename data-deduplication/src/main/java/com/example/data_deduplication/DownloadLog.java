package com.example.data_deduplication;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity

public class DownloadLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long fileId;
    private String fileName;
    private String downloaderEmail;
    private LocalDateTime downloadTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDownloaderEmail() {
		return downloaderEmail;
	}
	public void setDownloaderEmail(String downloaderEmail) {
		this.downloaderEmail = downloaderEmail;
	}
	public LocalDateTime getDownloadTime() {
		return downloadTime;
	}
	public void setDownloadTime(LocalDateTime downloadTime) {
		this.downloadTime = downloadTime;
	}
    
    
}