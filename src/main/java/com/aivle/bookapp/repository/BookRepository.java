package com.aivle.bookapp.repository;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    Optional<Long> findIdByTitleAndAuthor(String title, User author); // findId.. 이므로 반환값 Long으로 수정

    Optional<Book> findByTitleAndAuthor(String title, User author);

    // 교안 p.128: 쿼리 메서드 - 제목 일치 검색
    List<Book> findByTitle(String title);
    
    // 교안 p.129: Containing - 부분 일치 검색
    List<Book> findByTitleContaining(String keyword);

    // 최신 도서 3개 쿼리
    List<Book> findTop3ByOrderByCreatedAtDesc();

    // 인기 도서 3개 쿼리
    List<Book> findTop3ByOrderByLikeCountDesc();

    // 저자별 검색
    List<Book> findByAuthorContaining(String author);

    // 출판사별 검색
    List<Book> findByPublisherContaining(String publisher);

    // 컨텐츠별 검색
    List<Book> findByContentContaining(String content);

    // 태그별 검색
    List<Book> findByTagsContaining(String tags);

    // 전체 검색
    @Query("SELECT b FROM Book b WHERE " +
            "b.title LIKE %:keyword% OR " +
            "b.author.nickname LIKE %:keyword% OR " +
            "b.publisher LIKE %:keyword% OR " +
            "b.content LIKE %:keyword% OR " +
            "b.tags LIKE %:keyword%")
    List<Book> findByKeyword(@Param("keyword") String keyword);
}
