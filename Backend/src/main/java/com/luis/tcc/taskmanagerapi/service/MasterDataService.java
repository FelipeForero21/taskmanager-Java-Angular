package com.luis.tcc.taskmanagerapi.service;

import com.luis.tcc.taskmanagerapi.entity.TaskPriority;
import com.luis.tcc.taskmanagerapi.entity.TaskStatus;
import com.luis.tcc.taskmanagerapi.repository.TaskPriorityRepository;
import com.luis.tcc.taskmanagerapi.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MasterDataService {
    
    private final TaskStatusRepository taskStatusRepository;
    private final TaskPriorityRepository taskPriorityRepository;
    
    //MÉTODOS PARA ESTADOS
    
    public List<TaskStatus> getAllActiveStatuses() {
        return taskStatusRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }
    
    public Optional<TaskStatus> getStatusByName(String statusName) {
        return taskStatusRepository.findByStatusNameAndIsActiveTrue(statusName);
    }
    
    public Optional<TaskStatus> getStatusById(Integer statusId) {
        return taskStatusRepository.findById(statusId);
    }
    
    public boolean statusExists(String statusName) {
        return taskStatusRepository.existsByStatusName(statusName);
    }
    
    //MÉTODOS PARA PRIORIDADES
    
    public List<TaskPriority> getAllActivePriorities() {
        return taskPriorityRepository.findByIsActiveTrueOrderByPriorityLevelAsc();
    }
    
    public Optional<TaskPriority> getPriorityByName(String priorityName) {
        return taskPriorityRepository.findByPriorityNameAndIsActiveTrue(priorityName);
    }
    
    public Optional<TaskPriority> getPriorityByLevel(Integer priorityLevel) {
        return taskPriorityRepository.findByPriorityLevelAndIsActiveTrue(priorityLevel);
    }
    
    public Optional<TaskPriority> getPriorityById(Integer priorityId) {
        return taskPriorityRepository.findById(priorityId);
    }
    
    public boolean priorityExists(String priorityName) {
        return taskPriorityRepository.existsByPriorityName(priorityName);
    }
    
    //MÉTODOS PARA OBTENER DATOS MAESTROS COMPLETOS
    
    public MasterDataResponse getAllMasterData() {
        MasterDataResponse response = new MasterDataResponse();
        response.setStatuses(getAllActiveStatuses());
        response.setPriorities(getAllActivePriorities());
        return response;
    }
    
    //CLASE INTERNA PARA RESPUESTA
    
    public static class MasterDataResponse {
        private List<TaskStatus> statuses;
        private List<TaskPriority> priorities;
        
        // Getters y Setters
        public List<TaskStatus> getStatuses() {
            return statuses;
        }
        
        public void setStatuses(List<TaskStatus> statuses) {
            this.statuses = statuses;
        }
        
        public List<TaskPriority> getPriorities() {
            return priorities;
        }
        
        public void setPriorities(List<TaskPriority> priorities) {
            this.priorities = priorities;
        }
    }
} 