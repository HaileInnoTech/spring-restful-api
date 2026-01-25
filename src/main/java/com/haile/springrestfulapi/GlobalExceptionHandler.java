package com.haile.springrestfulapi;

import com.haile.springrestfulapi.helper.ApiResponse;
import com.haile.springrestfulapi.helper.exception.ResourceAlreadyExistsException;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> entityNotFound(EntityNotFoundException e) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({ResourceNotFoundException.class, ResourceAlreadyExistsException.class})
    public ResponseEntity<ApiResponse<String>> handleNotFound(RuntimeException e) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleBlank(ConstraintViolationException ex) {
        System.out.println("ConstraintViolationException: " + ex.getConstraintViolations());
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("Dữ liệu không hợp lệ");

        return ApiResponse.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("Tham số '%s' có giá trị '%s' không đúng định dạng.", ex.getName(), ex.getValue());
        return ApiResponse.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorList = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());
        String errors = String.join("; ", errorList);

        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, errors, null, "VALIDATION_ERROR");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateExceptions(DataIntegrityViolationException ex) {

        String errorMessage = "Email không thể duplicate";
        return ApiResponse.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Required request body is missing");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllException(Exception ex) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Lỗi chưa xác định" + ex.getMessage());
    }

}
