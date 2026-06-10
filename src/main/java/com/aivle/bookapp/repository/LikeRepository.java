package com.aivle.bookapp.repository;

import com.aivle.bookapp.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUser_UserIdAndBook_Id(String userId, Long bookId);
    boolean existsByUser_UserIdAndBook_id(String userId, Long bookId);
    long countByBook_Id(Long bookId);
}
