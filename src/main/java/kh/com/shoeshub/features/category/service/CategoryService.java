package kh.com.shoeshub.features.category.service;

import kh.com.shoeshub.features.category.Category;
import kh.com.shoeshub.features.category.dto.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {
    Category createCategory(CreateCategoryRequest request);
    Category getCategoryById(Short id);
    List<Category> getAllCategories();
    Category updateCategory(Short id, CreateCategoryRequest request);
    void deleteCategory(Short id);
}
