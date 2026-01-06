package com.assessment.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTaskRequest {

    @NotBlank(message = "title is required")
    @Size(min = 3, message = "title at least 3 characters")
    private String title;

    private Integer priority;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}