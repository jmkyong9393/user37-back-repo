package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.domain.Likes;
import com.aivle.bookapp.domain.User;
import com.aivle.bookapp.exception.BookAlreadyExistsException;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.repository.BookRepository;
import com.aivle.bookapp.repository.LikeRepository;
import com.aivle.bookapp.repository.UserRepository;
import com.aivle.bookapp.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    // 교안 p.171: 조회 메서드 - readOnly = true 최적화
    //반환형 Page<Book> 으로 수정
    @Transactional(readOnly = true)     // 검색어값 없을 시 작동
    public Page<Book> findAll(int page) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), 12,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Book> bookPage = bookRepository.findAll(pageable);

        if (bookPage.isEmpty() && bookPage.getTotalPages() > 0) {       // page 값 초과 시 예외 처리(마지막 페이지로 이동)
            Pageable lastPageable = PageRequest.of(bookPage.getTotalPages() - 1, 12,
                    Sort.by(Sort.Direction.DESC, "createdAt"));
            bookPage = bookRepository.findAll(lastPageable);
        }

        return bookPage;
    }

    @Transactional(readOnly = true)     // 검색 시 작동
    public  Page<Book> search(
            String searchType,  //검색 타입 (all, title, author 등등)
            String keyWord,     //검색 키워드
            String sortBy,      //정렬 기준 (등록 시간, 제목, 추천 수 등)
            String order,       //DESC/ASC
            int page            //페이징(페이지당 12행)
    ){
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        if (sortBy.equals("time"))
            sortBy = "createdAt";
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(Math.max(0, page-1), 12, sort);

        Specification<Book> spec = BookSpecification.search(searchType, keyWord);

        Page<Book> bookPage = bookRepository.findAll(spec, pageable);

        if (bookPage.isEmpty() && bookPage.getTotalPages() > 0) {   // page 값 초과 시 예외 처리
            Pageable lastPageable = PageRequest.of(bookPage.getTotalPages() - 1, 12, sort);
            bookPage = bookRepository.findAll(spec, lastPageable);
        }

        return bookPage;
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
        if (book.getAuthor() == null || book.getAuthor().getUserId() == null) {
            throw new IllegalArgumentException("저자 정보가 없습니다.");
        }
        User realUser = userRepository.findByUserId(book.getAuthor().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        book.setAuthor(realUser);
        if (bookRepository.findIdByTitleAndAuthor(book.getTitle(), realUser).isPresent()){
            throw new BookAlreadyExistsException(book.getTitle());
        }
        return bookRepository.save(book);
    }

    // 교안 p.166: PATCH 부분 수정 비즈니스 로직
    @Transactional
    public Book update(Long id, Book book, String loginUserId) {
        Book existing = findById(id);

        // 책의 작가와 로그인한 유저의 아이디 대조
        if (existing.getAuthor() == null || !existing.getAuthor().getUserId().equals(loginUserId)) {
            throw new IllegalArgumentException("자신이 등록한 책만 수정할 수 있습니다.");
        }

        User authorUser = book.getAuthor() != null ?
                book.getAuthor() : existing.getAuthor();

        Book existsBook = bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor()).orElse(null);
        if (!existsBook.getId().equals(id))
            throw new BookAlreadyExistsException("이미 작성하신 제목의 도서입니다.");
        
        if (book.getTitle() != null) {
            existing.setTitle(book.getTitle());
        }
        if (book.getAuthor() != null) {
            User realUser = userRepository.findByUserId(book.getAuthor().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            existing.setAuthor(realUser);
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
    public void delete(Long id, String loginUserId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        // 책의 작가와 로그인한 유저의 아이디 대조
        if (book.getAuthor() == null || loginUserId == null ||
                !book.getAuthor().getUserId().trim().equalsIgnoreCase(loginUserId.trim())) {
            throw new IllegalArgumentException("자신이 등록한 책만 삭제할 수 있습니다.");
        }
        likeRepository.deleteByBook(book);

        bookRepository.deleteById(id);
    }

    // 교안 p.129: 쿼리 메서드 검색
    @Transactional(readOnly = true)
    public List<Book> search(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
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
  
    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByAuthor (String author) {
        return bookRepository.findByAuthorContaining(author);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByPublisher (String publisher) {
        return bookRepository.findByPublisherContaining(publisher);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByContent (String content) {
        return bookRepository.findByContentContaining(content);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByTags (String tags) {
        return bookRepository.findByTagsContaining(tags);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByKeyword (String keyword) {
        return bookRepository.findByKeyword(keyword);
    }

    @Transactional
    public Book like(Long bookId, String userId, String loginUserId) {
        Book book = findById(bookId);

        User user = userRepository.findByUserId(loginUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + loginUserId));

        Likes existingLike = likeRepository
                .findByUser_UserIdAndBook_Id(loginUserId, bookId)
                .orElse(null);

        Integer currentLikeCount = book.getLikeCount();

        if (currentLikeCount == null) {
            currentLikeCount = 0;
        }

        if (existingLike != null) {
            likeRepository.delete(existingLike);
            book.setLikeCount(Math.max(currentLikeCount - 1, 0));
        } else {
            Likes likes = Likes.builder()
                    .user(user)
                    .book(book)
                    .build();

            likeRepository.save(likes);
            book.setLikeCount(currentLikeCount + 1);
        }

        return bookRepository.save(book);
    }
}
