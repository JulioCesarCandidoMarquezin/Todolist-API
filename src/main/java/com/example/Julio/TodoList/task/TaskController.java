package com.example.Julio.TodoList.task;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Julio.TodoList.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    InterfaceTaskRepository repository;
    
    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel model, HttpServletRequest request)
    {
        model.setUserId((UUID) request.getAttribute("userId"));

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(model.getStartAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser depois do dia atual");
        }
        if(model.getStartAt().isAfter(model.getEndAt()) || model.getStartAt().isEqual(model.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser antes da data de finalização");
        }

        var task = this.repository.save(model);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request)
    {
        var userId = request.getAttribute("userId");
        var tasks = this.repository.findByUserId((UUID) userId);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel model, HttpServletRequest request, @PathVariable UUID id)
    {
        var task = this.repository.findById(id).orElse(null);

        if(task == null) {
            return ResponseEntity.badRequest().body("Tarefa não encontrada");
        }

        var userId = request.getAttribute("userId");

        if(task.getUserId().equals(userId)) {
            Utils.copyNonNullProperties(model, task);
            var taskUpdated = this.repository.save(task);
            return ResponseEntity.ok().body(taskUpdated);
        }

        return ResponseEntity.badRequest().body("Usuário não tem permissão para alterar essa tarefa");
    }
}
