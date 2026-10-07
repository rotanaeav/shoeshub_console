package kh.com.shoeshub.features.category.service;

import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.exception.ValidationException;
import kh.com.shoeshub.features.category.Category;
import kh.com.shoeshub.features.category.dto.CreateCategoryRequest;
import kh.com.shoeshub.features.category.repository.CategoryRepository;
import kh.com.shoeshub.features.category.repository.CategoryRepositoryImpl;
import kh.com.shoeshub.features.user.UserRole;

import java.util.List;
import java.util.Optional;

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository = new CategoryRepositoryImpl();
    private final AuthorizationService authorizationService;

    public CategoryServiceImpl(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public Category createCategory(CreateCategoryRequest request) {
        requireStaff();
        validate(request);

        if (categoryRepository.findByName(request.getName().trim()).isPresent()) {
            throw new ValidationException("Category name already exists.");
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .description(trimOrNull(request.getDescription()))
                .build();

        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Short id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Category id must be greater than 0.");
        }
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(Short id, CreateCategoryRequest request) {
        requireStaff();
        Category existing = getCategoryById(id);
        validate(request);

        // the new name must not belong to a different category
        Optional<Category> sameName = categoryRepository.findByName(request.getName().trim());
        if (sameName.isPresent() && !sameName.get().getId().equals(id)) {
            throw new ValidationException("Category name already exists.");
        }

        existing.setName(request.getName().trim());
        existing.setDescription(trimOrNull(request.getDescription()));

        return categoryRepository.update(id, existing);
    }

    @Override
    public void deleteCategory(Short id) {
        requireStaff();
        getCategoryById(id); // throws NotFoundException if missing

        if (categoryRepository.hasProducts(id)) {
            throw new ValidationException("Cannot delete this category because it still has products.");
        }
        categoryRepository.deleteById(id);
    }

    // ----------------------------------------------------------------- helpers

    // Only ADMIN and SELLER may change categories (reading them is open to everyone)
    private void requireStaff() {
        authorizationService.requireAnyRole(UserRole.ADMIN, UserRole.SELLER);
    }

    private void validate(CreateCategoryRequest request) {
        if (request == null) {
            throw new ValidationException("Category data is required.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("Category name is required.");
        }
        if (request.getName().trim().length() > 100) {
            throw new ValidationException("Category name must be at most 100 characters.");
        }
        if (request.getDescription() != null && request.getDescription().trim().length() > 255) {
            throw new ValidationException("Description must be at most 255 characters.");
        }
    }

    private String trimOrNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
