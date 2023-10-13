package com.example.Julio.TodoList.task;

import java.util.UUID;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterfaceTaskRepository extends JpaRepository<TaskModel, UUID>{
    List<TaskModel> findByUserId(UUID userId);
    TaskModel findByIdAndUserId(UUID id, UUID userId);
}
