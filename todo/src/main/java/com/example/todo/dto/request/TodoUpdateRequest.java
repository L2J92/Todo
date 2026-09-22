package com.example.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TodoUpdateRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @NotBlank(message = "Description cannot be blank")
        boolean completed
) {

}
