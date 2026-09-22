package com.example.todo.service;

import com.example.todo.dto.request.TodoCreateRequest;
import com.example.todo.dto.request.TodoUpdateRequest;
import com.example.todo.dto.response.TodoResponse;
import com.example.todo.entity.Todo;
import com.example.todo.exception.NotFoundException;
import com.example.todo.repository.TodoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public Page<TodoResponse> getList(Pageable pageable) {
        return todoRepository.findAll(pageable).map(TodoResponse::from);
    }

    @Transactional
    public TodoResponse getDetail(Long id) {
        return todoRepository.findById(id)
                .map(TodoResponse::from)
                .orElseThrow(() -> new NotFoundException("Todo not found"));
    }

    @Transactional
    public void add(TodoCreateRequest request) {
        todoRepository.save(Todo.create(request.title(), false));
    }

    @Transactional
    public void update(Long id, TodoUpdateRequest request) {
        Todo todo = findTodo(id);
        todo.update(request.title(), request.completed());
    }

    @Transactional
    public void delete(Long id) {
        findTodo(id);
        todoRepository.deleteById(id);
    }

    private Todo findTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Todo not found"));
        return todo;
    }
}
