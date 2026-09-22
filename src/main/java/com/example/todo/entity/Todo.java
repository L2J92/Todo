package com.example.todo.entity;


import com.example.todo.dto.request.TodoUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "todo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    public Todo(String title, boolean completed) {
        this.title = title;
        this.completed = completed;
    }

    public static Todo create(String title, boolean completed) {
        return new Todo(title, completed);
    }

    public void update(String title, boolean completed) {
        this.title = title;
        this.completed = completed;
    }
}
