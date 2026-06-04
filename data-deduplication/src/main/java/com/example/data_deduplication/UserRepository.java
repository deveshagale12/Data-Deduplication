package com.example.data_deduplication;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT l FROM DownloadLog l WHERE l.fileId IN (SELECT f.id FROM UserFile f WHERE f.userId = :ownerId)")
    List<DownloadLog> findAllByOwnerId(@Param("ownerId") Long ownerId);
}