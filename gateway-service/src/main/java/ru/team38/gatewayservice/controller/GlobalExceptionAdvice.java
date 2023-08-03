package ru.team38.gatewayservice.controller;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(FeignException.Forbidden.class)
    public ResponseEntity<String> handleFeignStatusException(FeignException e) {
        log.error(e.contentUTF8(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.contentUTF8());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> responseExceptionHandler(FeignException ex) {
        log.error(ex.contentUTF8(), ex);
        return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
    }
}
