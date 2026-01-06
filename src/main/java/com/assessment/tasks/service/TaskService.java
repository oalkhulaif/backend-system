package com.assessment.tasks.service;

import com.assessment.tasks.dto.CreateTaskRequest;
import com.assessment.tasks.dto.UpdateTaskRequest;
import com.assessment.tasks.model.Task;
import com.assessment.tasks.repository.TaskRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setPriority(request.getPriority());
        return taskRepository.save(task);
    }

    public List<Task> list(String status, Integer priority, int limit, int offset) {
        PageRequest pageable = PageRequest.of(offset / limit, limit);
        return taskRepository.findWithFilters(status, priority, pageable);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public Task update(Task task, UpdateTaskRequest request) {
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}