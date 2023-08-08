package ru.team38.userservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.team38.userservice.exceptions.CaptchaCreationException;
import ru.team38.userservice.exceptions.FriendsServiceException;
import ru.team38.userservice.exceptions.InvalidCaptchaException;
import ru.team38.userservice.exceptions.LogoutFailedException;
import ru.team38.userservice.exceptions.status.BadRequestException;
import ru.team38.userservice.exceptions.status.UnauthorizedException;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> userNotFoundHandler(UsernameNotFoundException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account does not exist.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> badCredentialsHandler(BadCredentialsException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> unauthorizedHandler(UnauthorizedException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in.");
    }

    @ExceptionHandler(LogoutFailedException.class)
    public ResponseEntity<String> logoutFailedHandler(LogoutFailedException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout failed.");
    }

    @ExceptionHandler(CaptchaCreationException.class)
    public ResponseEntity<String> captchaCreationFailedHandler(CaptchaCreationException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Captcha creation failed.");
    }

    @ExceptionHandler(InvalidCaptchaException.class)
    public ResponseEntity<String> captchaCreationFailedHandler(InvalidCaptchaException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(FriendsServiceException.class)
    public ResponseEntity<String> friendsServiceExceptionHandler(FriendsServiceException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> badRequestHandler(BadRequestException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
