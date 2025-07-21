package com.luis.tcc.taskmanagerapi.service;

import com.luis.tcc.taskmanagerapi.entity.Category;
import com.luis.tcc.taskmanagerapi.entity.User;
import com.luis.tcc.taskmanagerapi.repository.CategoryRepository;
import com.luis.tcc.taskmanagerapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderByCategoryNameAsc();
    }
    
} 