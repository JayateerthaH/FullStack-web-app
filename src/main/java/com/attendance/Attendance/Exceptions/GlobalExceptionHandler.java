package com.attendance.Attendance.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>>handleInvalidInput(MethodArgumentNotValidException ex){
        Map<String,String>response=new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((errors)->{
            String field = ((FieldError) errors).getField();
            String defaultMessage = errors.getDefaultMessage();
            response.put(field,defaultMessage);
        });
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,String>>handleInvalidInput(ResourceNotFoundException ex){
        Map<String,String>map = new HashMap<>();
        map.put("Error", ex.getMessage());
        return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
    }

}
