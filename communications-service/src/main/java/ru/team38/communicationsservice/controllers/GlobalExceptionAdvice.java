package ru.team38.communicationsservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ru.team38.communicationsservice.exceptions.BadRequestException;
import ru.team38.communicationsservice.exceptions.BadRequestPostExceptions;
import ru.team38.communicationsservice.exceptions.NotFoundPostExceptions;

@ControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(NotFoundPostExceptions.class)
    public ResponseEntity<String> notFoundPostHandler(NotFoundPostExceptions ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found.");
    }

    @ExceptionHandler(BadRequestPostExceptions.class)
    public ResponseEntity<String> badRequestPostHandler(NotFoundPostExceptions ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request not correct.");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> badRequestHandler(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleFileSizeLimitExceeded(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File must be smaller " + exc.getMaxUploadSize());
    }
}
