package com.example.academicwarning;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(StudentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleStudentNotFound(StudentNotFoundException e){
        return Map.of("code",404,"message",e.getMessage());
    }
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String,Object> handleUnautherized(UnauthorizedException e){
        return Map.of("code",401,"message",e.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();
        for (FieldError err : e.getBindingResult().getFieldErrors()) {
            if (sb.length() > 0) {
                sb.append("；");
            }
            sb.append(err.getDefaultMessage());
        }
        return Map.of("code", 400, "message", sb.toString());
    }
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(BadRequestException e) {
        return Map.of("code", 400, "message", e.getMessage());
    }
    @ExceptionHandler(AiServiceException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String,Object> handleAiService(AiServiceException e){
        return Map.of("code", 503, "message", e.getMessage());
    }
    @ExceptionHandler(CourseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleCourseNotFound(CourseNotFoundException e) {
        return Map.of("code", 404, "message", e.getMessage());
    }
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleDuplicateKey(DuplicateKeyException e) {
        return Map.of("code", 400, "message", "数据已存在");
    }
}
