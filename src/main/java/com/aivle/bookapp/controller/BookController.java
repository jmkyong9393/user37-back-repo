package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    // 교안 p.157: GET /books - 목록 조회
    @GetMapping
    public List<Book> getAll() {
        return bookService.findAll();
    }

    // 교안 p.158: GET /books/{id} - 상세 조회
    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id) {
        return bookService.findById(id);
    }

    // 교안 p.162: POST /books - 신규 생성 (201 Created)
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        Book saved = bookService.create(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 교안 p.164: PATCH /books/{id} - 부분 수정
    @PatchMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        return bookService.update(id, book);
    }

    // 교안 p.31, p.57: PATCH /books/{id}/cover - AI 표지 이미지 저장
    @PatchMapping("/{id}/cover")
    public Book updateCover(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return bookService.updateCoverImage(id, request.get("coverImageUrl"));
    }

    // 교안 p.167: DELETE /books/{id} - 삭제 (204 No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 교안 p.129, p.130: GET /books/search - 검색
    @GetMapping("/search")
    public List<Book> search(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String title,
                             @RequestParam(required = false) String author) {
        if (title != null && author != null) {
            return bookService.searchByTitleAndAuthor(title, author);
        }
        return bookService.search(keyword != null ? keyword : "");
    }
}
