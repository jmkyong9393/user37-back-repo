package com.aivle.bookapp.exception;

public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String msg) {
        super("Book already exists : " + msg);
    }
}
