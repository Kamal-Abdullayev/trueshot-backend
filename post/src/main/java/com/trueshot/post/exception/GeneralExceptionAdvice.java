package com.trueshot.post.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GeneralExceptionAdvice {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handle(ResourceNotFoundException exception) {
        return new ResponseEntity<>(new CustomErrorResponse(
                new Error("E1000",
                        List.of(exception.getMessage())
                )), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<CustomErrorResponse> handle(InvalidParameterException exception) {
        return new ResponseEntity<>(new CustomErrorResponse(
                new Error("E1001",
                        List.of(exception.getMessage()))
        ), HttpStatus.BAD_REQUEST);
    }

}