package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.dto.request.BookCreateRequest;
import com.aivle.bookapp.dto.request.BookUpdateRequest;
import com.aivle.bookapp.dto.request.CoverImageRequest;
import com.aivle.bookapp.dto.request.LikeRequest;
import com.aivle.bookapp.dto.response.BookPageResponse;
import com.aivle.bookapp.dto.response.BookResponse;
import com.aivle.bookapp.service.BookService;
import com.aivle.bookapp.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final JwtUtil jwtUtil;

    // 교안 p.157: GET /books - 목록 조회
    @GetMapping
    public BookPageResponse getAll(
            @RequestParam(defaultValue = "all") String searchType,
            @RequestParam(defaultValue = "") String keyWord,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") int page
    ) {
        Page<Book> bookPage;
        if(keyWord.isBlank())
            bookPage = bookService.findAll(page);
        else{
            bookPage = bookService.search(searchType, keyWord, sortBy, order, page);
        }

        if(bookPage.getTotalPages()<page)
            page = bookPage.getTotalPages();

        List<BookResponse> responseList = toResponseList(bookPage.getContent());

        return BookPageResponse.builder()
                .content(responseList)
                .totalPages(bookPage.getTotalPages())
                .currentPage(page)
                .build();
    }

    // 교안 p.158: GET /books/{id} - 상세 조회
    @GetMapping("/{id}")
    public BookResponse getBook(@PathVariable Long id) {
        return BookResponse.from(bookService.findById(id));
    }

    // 신규 도서 3개
    @GetMapping("/new")
    public List<BookResponse> getNewBooks() {
        return toResponseList(bookService.searchNew());
    }

    // 인기 도서 3개
    @GetMapping("/popular")
    public List<BookResponse> getPopularBooks() {
        return toResponseList(bookService.searchPopular());
    }

    // 교안 p.162: POST /books - 신규 생성 (201 Created)
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest request) {
        Book saved = bookService.create(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(BookResponse.from(saved));
    }

    // 교안 p.164: PATCH /books/{id} - 부분 수정
    @PatchMapping("/{id}")
    public BookResponse updateBook(
            @PathVariable Long id,
            @RequestBody BookUpdateRequest request,
            @AuthenticationPrincipal String loginUserId) {
        return BookResponse.from(bookService.update(id, request.toEntity(), loginUserId));
    }

    // 교안 p.167: DELETE /books/{id} - 삭제 (204 No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, @AuthenticationPrincipal String loginUserId) {

        bookService.delete(id, loginUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/title")
    public List<BookResponse> searchByTitle(@RequestParam String title) {
        return toResponseList(bookService.searchByTitle(title));
    }

    @GetMapping("/search/author")
    public List<BookResponse> searchByAuthor(@RequestParam String author) {
        return toResponseList(bookService.searchByAuthor(author));
    }

    @GetMapping("/search/publisher")
    public List<BookResponse> searchByPublisher(@RequestParam String publisher) {
        return toResponseList(bookService.searchByPublisher(publisher));
    }

    @GetMapping("/search/content")
    public List<BookResponse> searchByContent(@RequestParam String content) {
        return toResponseList(bookService.searchByContent(content));
    }

    @GetMapping("/search/tags")
    public List<BookResponse> searchByTags(@RequestParam String tags) {
        return toResponseList(bookService.searchByTags(tags));
    }

    @GetMapping("/search/keyword")
    public List<BookResponse> searchByKeyword(@RequestParam String keyword) {
        return toResponseList(bookService.searchByKeyword(keyword));
    }

    // 교안 p.31, p.57: PATCH /books/{id}/cover - AI 표지 이미지 저장
    @PatchMapping("/{id}/cover")
    public BookResponse updateCoverImage(@PathVariable Long id, @Valid @RequestBody CoverImageRequest request) {
        return BookResponse.from(bookService.updateCoverImage(id, request.getCoverImageUrl()));
    }

    private List<BookResponse> toResponseList(List<Book> books) {
        return books.stream()
                .map(BookResponse::from)
                .toList();
    }
    @PatchMapping("/{id}/like")
    public BookResponse like(
            @PathVariable Long id,
            @RequestBody LikeRequest request
    ) {
        return BookResponse.from(bookService.like(id, request.getUserId()));
    }
}
