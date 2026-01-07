package com.assessment.tasks.controller;

import com.assessment.tasks.dto.*;
import com.assessment.tasks.exception.TaskNotFoundException;
import com.assessment.tasks.model.IdempotencyRecord;
import com.assessment.tasks.model.Task;
import com.assessment.tasks.service.IdempotencyService;
import com.assessment.tasks.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    public TaskController(TaskService taskService, IdempotencyService idempotencyService, ObjectMapper objectMapper) {
        this.taskService = taskService;
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateTaskRequest request) throws Exception {

        if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
            if (idempotencyKey.length() > 255) {
                throw new IllegalArgumentException("Idempotency-Key must not exceed 255 characters");
            }

            String payloadHash = idempotencyService.hashPayload(request);
            Optional<IdempotencyRecord> existing = idempotencyService.find(idempotencyKey);

            if (existing.isPresent()) {
                idempotencyService.validate(idempotencyKey, payloadHash);
                TaskResponse cached = objectMapper.readValue(existing.get().getResponse(), TaskResponse.class);
                return ResponseEntity.status(HttpStatus.CREATED).body(cached);
            }

            Task task = taskService.create(request);
            TaskResponse response = new TaskResponse(task);
            String responseJson = objectMapper.writeValueAsString(response);
            idempotencyService.save(idempotencyKey, payloadHash, responseJson);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        Task task = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskResponse(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer priority,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        if (limit > 100)
            limit = 100;

        List<Task> tasks = taskService.list(status, priority, limit, offset);
        List<TaskResponse> response = tasks.stream().map(TaskResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {

        Task task = taskService.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        Task updated = taskService.update(task, request);
        return ResponseEntity.ok(new TaskResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        taskService.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskService.delete(id);
        return ResponseEntity.ok(new DeleteResponse(true));
    }
}