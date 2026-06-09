package com.aivle.bookapp.dto.response;

import com.aivle.bookapp.domain.Book;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String content;
    private String tags;
    private String coverImageUrl;
    private Integer likeCount;
    private String createdAt;
    private String updatedAt;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .content(book.getContent())
                .tags(book.getTags())
                .coverImageUrl(book.getCoverImageUrl())
                .likeCount(book.getLikeCount())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
