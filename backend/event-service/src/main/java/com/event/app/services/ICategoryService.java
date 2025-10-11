package com.event.app.services;

import java.util.List;
import java.util.Optional;

import com.event.app.dtos.CategoryDTO;
import com.event.app.models.Category;

public interface ICategoryService {

    Category createCategory(CategoryDTO categoryDTO);

    Optional<Category> getCategoryById(Long id);

    List<Category> getAllCategories();

    Category updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);
}

