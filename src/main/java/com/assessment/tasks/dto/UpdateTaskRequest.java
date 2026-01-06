package com.assessment.tasks.dto;

import jakarta.validation.constraints.Size;

public class UpdateTaskRequest {

    @Size(min = 3, message = "title at least 3 characters")
    private String title;

    private String status;

    private Integer priority;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}