package com.example.data_deduplication;

import java.util.List;

import org.eclipse.angus.mail.imap.protocol.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface DownloadLogRepository extends JpaRepository<DownloadLog, ID>{

	// This query joins DownloadLog with UserFile to find logs for a specific owner
    @Query("SELECT l FROM DownloadLog l WHERE l.fileId IN (SELECT f.id FROM UserFile f WHERE f.userId = :ownerId) ORDER BY l.downloadTime DESC")
    List<DownloadLog> findAllByOwnerId(@Param("ownerId") Long ownerId);

}
