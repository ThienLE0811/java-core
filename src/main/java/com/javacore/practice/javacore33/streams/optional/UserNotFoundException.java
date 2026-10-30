package com.javacore.practice.javacore33.streams.optional;

/**
 * Bài 28 - Unchecked exception cho orElseThrow. Trong Spring Boot, ngoại lệ kiểu này
 * thường được @ResponseStatus(HttpStatus.NOT_FOUND) hoặc bắt ở @RestControllerAdvice
 * để đổi thành HTTP 404.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("Không tìm thấy người dùng với email: " + email);
    }
}
