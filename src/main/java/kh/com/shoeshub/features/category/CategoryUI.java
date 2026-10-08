package kh.com.shoeshub.features.category;

import kh.com.shoeshub.features.category.dto.CreateCategoryRequest;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.utils.TableUtil;
import org.nocrala.tools.texttablefmt.Table;

import java.util.List;

public class CategoryUI {

    public void displayCategoryMenu() {
        OutputUtil.printHeader("CATEGORY MANAGEMENT");
        OutputUtil.println(" [1] List Categories");
        OutputUtil.println(" [2] Add Category");
        OutputUtil.println(" [3] Update Category");
        OutputUtil.println(" [4] Delete Category");
        OutputUtil.println(" [0] Back");
    }

    public CreateCategoryRequest getCategoryInput() {
        OutputUtil.printSubHeader("Category Information");
        String name = InputUtil.readRequiredText("Category name");
        String description = InputUtil.readText("Description (optional)");

        return CreateCategoryRequest.builder()
                .name(name)
                .description(description)
                .build();
    }

    public Short readCategoryId() {
        return (short) InputUtil.readInt("Category ID", 1, Short.MAX_VALUE);
    }

    public void displayCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            OutputUtil.printInfo("No categories found.");
            return;
        }

        OutputUtil.printSubHeader("CATEGORY LIST");
        Table table = TableUtil.createTable(3, "ID", "NAME", "DESCRIPTION");

        for (Category c : categories) {
            table.addCell(String.valueOf(c.getId()));
            table.addCell(c.getName());
            table.addCell(c.getDescription() != null ? c.getDescription() : "-");
        }
        TableUtil.render(table);
    }
}
