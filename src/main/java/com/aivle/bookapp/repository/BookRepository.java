package com.aivle.bookapp.repository;

import com.aivle.bookapp.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    
    // 교안 p.128: 쿼리 메서드 - 제목 일치 검색
    List<Book> findByTitle(String title);
    
    // 교안 p.129: Containing - 부분 일치 검색
    List<Book> findByTitleContaining(String keyword);
    
    // 교안 p.130: And - 복합 조건 검색
    List<Book> findByTitleAndAuthor(String title, String author);

    // 최신 도서 3개 쿼리
    List<Book> findTop3ByOrderByCreatedAtDesc();

    // 인기 도서 3개 쿼리
    List<Book> findTop3ByOrderByLikeCountDesc();
}
