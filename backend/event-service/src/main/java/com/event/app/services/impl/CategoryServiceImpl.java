package com.event.app.services.impl;

import com.event.app.dtos.CategoryDTO;
import com.event.app.models.Category;
import com.event.app.entities.CategoryEntity;
import com.event.app.exceptions.CategoryNotFoundException;
import com.event.app.repositories.CategoryRepository;
import com.event.app.services.ICategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);

        category.setActive(true);
        CategoryEntity entity = modelMapper.map(category, CategoryEntity.class);
        CategoryEntity saved = categoryRepository.save(entity);

        return modelMapper.map(saved, Category.class);
    }

    @Override
    public Optional<Category> getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .filter(CategoryEntity::getActive)
                .map(entity -> modelMapper.map(entity, Category.class));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(entity -> modelMapper.map(entity, Category.class))
                .collect(Collectors.toList());
    }

    @Override
    public Category updateCategory(UUID id, CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
            .filter(CategoryEntity::getActive)
            .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryEntity.setName(categoryDTO.getName());
        categoryEntity.setDescription(categoryDTO.getDescription());

        CategoryEntity categoryUpdated = categoryRepository.save(categoryEntity);
        return modelMapper.map(categoryUpdated, Category.class);
    }

    @Override
    public void deleteCategory(UUID id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
            .filter(CategoryEntity::getActive)
            .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryEntity.setActive(false); 
        categoryRepository.save(categoryEntity);
    }
}

