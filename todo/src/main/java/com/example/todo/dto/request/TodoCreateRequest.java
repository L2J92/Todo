package com.example.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TodoCreateRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @NotNull(message = "Completed cannot be null")
        boolean completed
) {

}
