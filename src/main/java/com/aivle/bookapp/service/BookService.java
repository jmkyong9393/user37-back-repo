package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // 교안 p.171: 조회 메서드 - readOnly = true 최적화
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // 교안 p.170: CUD 메서드 - @Transactional 적용
    @Transactional
    public Book create(Book book) {
        String now = now();
        if (book.getCreatedAt() == null) {
            book.setCreatedAt(now);
        }
        if (book.getUpdatedAt() == null) {
            book.setUpdatedAt(now);
        }
        if (book.getLikeCount() == null) {
            book.setLikeCount(0);
        }
        return bookRepository.save(book);
    }

    // 교안 p.166: PATCH 부분 수정 비즈니스 로직
    @Transactional
    public Book update(Long id, Book book) {
        Book existing = findById(id);
        
        if (book.getTitle() != null) {
            existing.setTitle(book.getTitle());
        }
        if (book.getAuthor() != null) {
            existing.setAuthor(book.getAuthor());
        }
        if (book.getPublisher() != null) {
            existing.setPublisher(book.getPublisher());
        }
        if (book.getContent() != null) {
            existing.setContent(book.getContent());
        }
        if (book.getTags() != null) {
            existing.setTags(book.getTags());
        }
        if (book.getCoverImageUrl() != null) {
            existing.setCoverImageUrl(book.getCoverImageUrl());
        }
        if (book.getLikeCount() != null) {
            existing.setLikeCount(book.getLikeCount());
        }
        if (book.getCreatedAt() != null) {
            existing.setCreatedAt(book.getCreatedAt());
        }
        if (book.getUpdatedAt() != null) {
            existing.setUpdatedAt(book.getUpdatedAt());
        } else {
            existing.setUpdatedAt(now());
        }
        
        return bookRepository.save(existing);
    }

    // 교안 p.167: DELETE 삭제 비즈니스 로직
    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }

    // 교안 p.129: 쿼리 메서드 검색
    @Transactional(readOnly = true)
    public List<Book> search(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
    }

    // 교안 p.130: 복합 조건 검색
    @Transactional(readOnly = true)
    public List<Book> searchByTitleAndAuthor(String title, String author) {
        return bookRepository.findByTitleAndAuthor(title, author);
    }

    // 교안 p.57: AI 표지 생성 결과(Data URL)를 도서 정보에 저장
    @Transactional
    public Book updateCoverImage(Long id, String coverImageUrl){
        if (coverImageUrl == null || coverImageUrl.isBlank()) {
            throw new IllegalArgumentException("coverImageUrl is required");
        }
        Book book = findById(id);
        book.setCoverImageUrl(coverImageUrl);
        book.setUpdatedAt(now());
        return bookRepository.save(book);
    }
    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // 최신 도서 3개
    @Transactional(readOnly = true)
    public List<Book> searchNew(){
        return bookRepository.findTop3ByOrderByCreatedAtDesc();
    }

    // 인기 도서 3개
    @Transactional(readOnly = true)
    public List<Book> searchPopular(){
        return bookRepository.findTop3ByOrderByLikeCountDesc();
    }
}
