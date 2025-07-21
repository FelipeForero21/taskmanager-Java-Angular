package com.luis.tcc.taskmanagerapi.service;

import com.luis.tcc.taskmanagerapi.entity.TaskPriority;
import com.luis.tcc.taskmanagerapi.entity.TaskStatus;
import com.luis.tcc.taskmanagerapi.repository.TaskPriorityRepository;
import com.luis.tcc.taskmanagerapi.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterDataService {
    
    private final TaskStatusRepository taskStatusRepository;
    private final TaskPriorityRepository taskPriorityRepository;
    
    //MÉTODOS PARA ESTADOS
    
    public List<TaskStatus> getAllActiveStatuses() {
        return taskStatusRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }
    
    //MÉTODOS PARA PRIORIDADES
    
    public List<TaskPriority> getAllActivePriorities() {
        return taskPriorityRepository.findByIsActiveTrueOrderByPriorityLevelAsc();
    }
    
} 