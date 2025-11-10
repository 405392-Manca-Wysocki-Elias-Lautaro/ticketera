package com.event.app.controllers;

import com.event.app.dtos.CategoryDTO;
import com.event.app.dtos.response.ApiResponse;
import com.event.app.models.Category;
import com.event.app.services.ICategoryService;
import com.event.app.utils.ApiResponseFactory;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final ICategoryService categoryService;
    private final ModelMapper modelMapper;

    public CategoryController(ICategoryService categoryService, ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryService.createCategory(categoryDTO);
        CategoryDTO response = modelMapper.map(category, CategoryDTO.class);
        return ApiResponseFactory.created("Category created successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable UUID id) {
        return categoryService.getCategoryById(id)
                .map(category -> ApiResponseFactory.success("Category retrieved successfully", 
                        modelMapper.map(category, CategoryDTO.class)))
                .orElse(ApiResponseFactory.notFound("Category not found with ID: " + id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories().stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
        return ApiResponseFactory.success("Categories retrieved successfully", categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryDTO categoryDTO) {
        Category updated = categoryService.updateCategory(id, categoryDTO);
        CategoryDTO response = modelMapper.map(updated, CategoryDTO.class);
        return ApiResponseFactory.success("Category updated successfully", response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ApiResponseFactory.success("Category deleted successfully");
    }
}

