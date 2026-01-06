package com.assessment.tasks.repository;

import com.assessment.tasks.model.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE (:status IS NULL OR t.status = :status) AND (:priority IS NULL OR t.priority = :priority) ORDER BY t.createdAt DESC")
    List<Task> findWithFilters(@Param("status") String status, @Param("priority") Integer priority, Pageable pageable);
}