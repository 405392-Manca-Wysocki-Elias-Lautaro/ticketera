package com.event.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.event.app.dtos.CategoryDTO;
import com.event.app.models.Category;

public interface ICategoryService {

    Category createCategory(CategoryDTO categoryDTO);

    Optional<Category> getCategoryById(UUID id);

    List<Category> getAllCategories();

    Category updateCategory(UUID id, CategoryDTO categoryDTO);

    void deleteCategory(UUID id);
}

