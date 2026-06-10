package com.aivle.bookapp.exception;

public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String title) {
        super("Book already exists : title=" + title);
    }
}
