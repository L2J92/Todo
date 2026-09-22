package com.example.todo.controller;


import com.example.todo.dto.request.TodoCreateRequest;
import com.example.todo.dto.request.TodoUpdateRequest;
import com.example.todo.dto.response.TodoResponse;
import com.example.todo.service.TodoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
@Tag(name = "Todo", description = "Todo 관리 API")
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public Page<TodoResponse> getList(Pageable pageable) {
        return todoService.getList(pageable);
    }

    @GetMapping("/{id}")
    public TodoResponse getDetail(@PathVariable Long id) {
        return todoService.getDetail(id);
    }

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody TodoCreateRequest request) {
        todoService.add(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody TodoUpdateRequest request) {
        todoService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.ok().build();
    }
}
