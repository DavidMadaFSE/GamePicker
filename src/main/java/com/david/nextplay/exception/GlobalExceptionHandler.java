package com.david.nextplay.exception;

import java.time.LocalDateTime;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
                ErrorResponse error = new ErrorResponse(
                                ex.getMessage(),
                                HttpStatus.NOT_FOUND.value(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(error);
        }

        @ExceptionHandler(GameConflictException.class)
        public ResponseEntity<ErrorResponse> handleGameAlreadyExists(GameConflictException ex) {
                ErrorResponse error = new ErrorResponse(
                                ex.getMessage(),
                                HttpStatus.CONFLICT.value(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(error);
        }

        @ExceptionHandler(UserConflictException.class)
        public ResponseEntity<ErrorResponse> handleUserConflict(UserConflictException ex) {
                ErrorResponse error = new ErrorResponse(
                                ex.getMessage(),
                                HttpStatus.CONFLICT.value(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(error);
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
                ErrorResponse error = new ErrorResponse(
                                ex.getMessage(),
                                HttpStatus.UNAUTHORIZED.value(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(error);
        }

        @ExceptionHandler(LibraryConflictException.class)
        public ResponseEntity<ErrorResponse> handleLibarayConflict(LibraryConflictException ex) {
                ErrorResponse error = new ErrorResponse(
                                ex.getMessage(),
                                HttpStatus.CONFLICT.value(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(error);
        }
}
