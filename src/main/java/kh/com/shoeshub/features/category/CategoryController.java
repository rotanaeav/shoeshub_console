package kh.com.shoeshub.features.category;

import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.features.category.dto.CreateCategoryRequest;
import kh.com.shoeshub.features.category.service.CategoryService;
import kh.com.shoeshub.features.category.service.CategoryServiceImpl;
//import kh.com.shoeshub.features.user.UserRole;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.features.auth.UserRole;

public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryUI categoryUI = new CategoryUI();

    // Role checks happen inside CategoryServiceImpl
    public CategoryController(AuthorizationService authorizationService) {
        this.categoryService = new CategoryServiceImpl(authorizationService);
    }

    public void showMenu() {
        boolean running = true;
        while (running) {
            categoryUI.displayCategoryMenu();
            int choice = InputUtil.readInt("Choose an option", 0, 4);

            switch (choice) {
                case 1 -> handleListCategories();
                case 2 -> handleCreateCategory();
                case 3 -> handleUpdateCategory();
                case 4 -> handleDeleteCategory();
                case 0 -> running = false;
            }
        }
    }

    public void handleListCategories() {
        run(() -> categoryUI.displayCategories(categoryService.getAllCategories()));
    }

    public void handleCreateCategory() {
        run(() -> {
            CreateCategoryRequest request = categoryUI.getCategoryInput();
            Category saved = categoryService.createCategory(request);
            OutputUtil.printSuccess("Category '" + saved.getName() + "' created with ID " + saved.getId() + ".");
        });
    }

    public void handleUpdateCategory() {
        run(() -> {
            categoryUI.displayCategories(categoryService.getAllCategories());
            Short id = categoryUI.readCategoryId();
            Category existing = categoryService.getCategoryById(id);

            OutputUtil.printInfo("Enter the new values for '" + existing.getName() + "'");
            CreateCategoryRequest request = categoryUI.getCategoryInput();
            categoryService.updateCategory(id, request);
            OutputUtil.printSuccess("Category updated.");
        });
    }

    public void handleDeleteCategory() {
        run(() -> {
            categoryUI.displayCategories(categoryService.getAllCategories());
            Short id = categoryUI.readCategoryId();
            Category existing = categoryService.getCategoryById(id);

            if (InputUtil.readConfirm("Delete category '" + existing.getName() + "'?")) {
                categoryService.deleteCategory(id);
                OutputUtil.printSuccess("Category deleted.");
            } else {
                OutputUtil.printInfo("Delete cancelled.");
            }
        });
    }

    // Runs one menu action: shows errors nicely, then waits for ENTER.
    // SecurityException comes from AuthorizationService (not logged in / no permission).
    private void run(Runnable action) {
        try {
            action.run();
        } catch (AppException | SecurityException e) {
            OutputUtil.printError(e.getMessage());
        } catch (Exception e) {
            OutputUtil.printError("Unexpected error: " + e.getMessage());
        } finally {
            InputUtil.pressEnter();
        }
    }
}