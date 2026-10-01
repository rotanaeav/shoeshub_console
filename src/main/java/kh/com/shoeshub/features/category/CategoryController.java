package kh.com.shoeshub.features.category;

import kh.com.shoeshub.features.category.service.CategoryService;
import kh.com.shoeshub.features.category.service.CategoryServiceImpl;

public class CategoryController {

    private final CategoryService categoryService = new CategoryServiceImpl();
    private final CategoryUI categoryUI = new CategoryUI();

    public void handleListCategories() {
        // TODO: Implement list categories action
    }
}
