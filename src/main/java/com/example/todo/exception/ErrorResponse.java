package com.example.todo.exception;

public record ErrorResponse(
        String code,
        String message
) {

}
