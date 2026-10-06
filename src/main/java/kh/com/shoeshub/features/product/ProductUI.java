package kh.com.shoeshub.features.product;

import kh.com.shoeshub.features.category.Category;
import kh.com.shoeshub.features.product.dto.CreateProductRequest;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.utils.TableUtil;
import org.nocrala.tools.texttablefmt.Table;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ProductUI {

    public void displayProductMenu() {
        // TODO: Display Product management menu
        OutputUtil.printHeader("Product Management");
        OutputUtil.println("""
                 [1]  LIST ALL PRODUCT
                 [2]  SEARCH PRODUCTS BY NAME
                 [3]  FILTER BY CATEGORY
                 [4]  VIEW PRODUCT DETAILS
                 [5]  ADD PRODUCT
                 [6]  UPDATE PRODUCT
                 [7]  DELETE PRODUCT
                 [8]  ADD VARIANT (size / color / stock)
                 [9] UPDATE STOCK
                 [0]  BACK
                 """);

    }

    // Shows the categories and asks for one ID that really exists in the list
    public Short readCategoryId(List<Category> categories) {
        OutputUtil.printSubHeader("AVAILABLE CATEGORIES");
        Table table = TableUtil.createTable(3, "ID", "NAME", "DESCRIPTION");
        for (Category c : categories) {
            table.addCell(String.valueOf(c.getId()));
            table.addCell(c.getName());
            table.addCell(c.getDescription() != null ? c.getDescription() : "-");
        }
        TableUtil.render(table);

        Set<Short> validIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        while (true) {
            short id = (short) InputUtil.readInt("Category ID", 1, Short.MAX_VALUE);
            if (validIds.contains(id)) {
                return id;
            }
            OutputUtil.printError("Please choose an ID from the list above.");
        }
    }

    public CreateProductRequest getProductInput(List<Category> categories) {
        OutputUtil.printSubHeader("Product Information");
        Short categoryId = readCategoryId(categories);
        String name = InputUtil.readRequiredText("Product name");
        String description = InputUtil.readText("Description (optional)");
        BigDecimal price = BigDecimal.valueOf(InputUtil.readDouble("Price ($)", 0.01));

        return CreateProductRequest.builder()
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .price(price)
                .build();
    }

    public ProductVariant getVariantInput(UUID productId) {
        OutputUtil.printSubHeader("Variant Information");
        BigDecimal size = BigDecimal.valueOf(InputUtil.readDouble("Size (e.g. 42 or 42.5)", 0.5));
        String color = InputUtil.readRequiredText("Color");
        int stock = InputUtil.readInt("Stock quantity", 0, 100000);

        return ProductVariant.builder()
                .productId(productId)
                .size(size)
                .color(color)
                .stockQuantity(stock)
                .build();
    }

    // ---------------------------------------------------------------- display
    // categoryNames maps category id -> category name (built by the controller)

    public void displayProducts(List<Product> products, Map<Short, String> categoryNames) {
        renderProductTable(products, "PRODUCT LIST", false, false, categoryNames);
    }

    public void displayProductsForAdmin(List<Product> products, Map<Short, String> categoryNames) {
        renderProductTable(products, "ADMIN PRODUCT", true, false, categoryNames);
    }

    // Numbered list used when the user has to pick one product
    public void displayProductChoices(List<Product> products, Map<Short, String> categoryNames) {
        renderProductTable(products, "SELECT A PRODUCT", false, true, categoryNames);
    }

    public void displayProductDetail(Product p, List<ProductVariant> variants, Map<Short, String> categoryNames) {
        OutputUtil.printSubHeader("PRODUCT DETAILS");
        OutputUtil.println(String.format(" SKU         : %s", p.getSku()));
        OutputUtil.println(String.format(" Name        : %s", p.getName()));
        OutputUtil.println(String.format(" Category    : %s", categoryLabel(p, categoryNames)));
        OutputUtil.println(String.format(" Price       : $%s", p.getPrice().toPlainString()));
        OutputUtil.println(String.format(" Status      : %s", p.isActive() ? "ACTIVE" : "INACTIVE"));
        OutputUtil.println(String.format(" Description : %s",
                p.getDescription() != null ? p.getDescription() : "-"));
        OutputUtil.println(String.format(" Created at  : %s", p.getCreatedAt()));

        int total = variants.stream().mapToInt(ProductVariant::getStockQuantity).sum();
        OutputUtil.println(String.format(" Total stock : %d", total));
        displayVariants(variants);
    }

    public void displayVariants(List<ProductVariant> variants) {
        if (variants == null || variants.isEmpty()) {
            OutputUtil.printInfo("No variants yet for this product.");
            return;
        }

        OutputUtil.printSubHeader("VARIANTS");
        Table table = TableUtil.createTable(5, "#", "SIZE", "COLOR", "STOCK", "STATUS");

        int no = 1;
        for (ProductVariant v : variants) {
            table.addCell(String.valueOf(no++));
            table.addCell(v.getSize().stripTrailingZeros().toPlainString());
            table.addCell(v.getColor());
            table.addCell(String.valueOf(v.getStockQuantity()));
            table.addCell(stockStatus(v.getStockQuantity()));
        }
        TableUtil.render(table);
    }

    // One row per variant, product columns shown on the first row only
    public void displayProductsWithVariants(List<Product> products,
                                            Map<Short, String> categoryNames,
                                            Map<UUID, List<ProductVariant>> variantsByProduct) {
        if (products == null || products.isEmpty()) {
            OutputUtil.printInfo("No products found in the catalog.");
            return;
        }

        OutputUtil.printSubHeader("PRODUCT LIST");
        Table table = TableUtil.createTable(8,
                "SKU", "NAME", "PRICE ($)", "CATEGORY", "STATUS", "SIZE", "COLOR", "STOCK");

        for (Product p : products) {
            List<ProductVariant> variants = variantsByProduct.getOrDefault(p.getId(), List.of());

            if (variants.isEmpty()) {
                addProductCells(table, p, categoryNames);
                table.addCell("-");
                table.addCell("-");
                table.addCell("-");
                continue;
            }

            boolean first = true;
            for (ProductVariant v : variants) {
                if (first) {
                    addProductCells(table, p, categoryNames);
                } else {
                    for (int i = 0; i < 5; i++) {
                        table.addCell(" ");   // keep the product columns empty
                    }
                }
                table.addCell(v.getSize().stripTrailingZeros().toPlainString());
                table.addCell(v.getColor());
                table.addCell(stockText(v.getStockQuantity()));
                first = false;
            }
        }
        TableUtil.render(table);
    }

    private void addProductCells(Table table, Product p, Map<Short, String> categoryNames) {
        table.addCell(p.getSku() != null ? p.getSku() : "-");
        table.addCell(p.getName() != null ? p.getName() : "-");
        table.addCell(p.getPrice() != null ? "$" + p.getPrice().toPlainString() : "$0.00");
        table.addCell(categoryLabel(p, categoryNames));
        table.addCell(p.isActive() ? "ACTIVE" : "INACTIVE");
    }

    private String stockText(int stock) {
        if (stock == 0) return "0 (OUT)";
        if (stock <= 5) return stock + " (LOW)";
        return String.valueOf(stock);
    }

    // ---------------------------------------------------------------- helpers

    private void renderProductTable(List<Product> products, String title, boolean showId,
                                    boolean numbered, Map<Short, String> categoryNames) {
        if (products == null || products.isEmpty()) {
            OutputUtil.printInfo("No products found in the catalog.");
            return;
        }

        OutputUtil.printSubHeader(title);

        List<String> headers = new ArrayList<>();
        if (numbered) headers.add("#");
        if (showId) headers.add("UUID");
        headers.addAll(List.of("SKU", "NAME", "PRICE ($)", "CATEGORY", "STATUS"));

        Table table = TableUtil.createTable(headers.size(), headers.toArray(new String[0]));

        int no = 1;
        for (Product p : products) {
            if (numbered) {
                table.addCell(String.valueOf(no++));
            }
            if (showId) {
                table.addCell(p.getId() != null ? p.getId().toString() : "-");
            }
            table.addCell(p.getSku() != null ? p.getSku() : "-");
            table.addCell(p.getName() != null ? p.getName() : "-");
            table.addCell(p.getPrice() != null ? "$" + p.getPrice().toPlainString() : "$0.00");
            table.addCell(categoryLabel(p, categoryNames));
            table.addCell(p.isActive() ? "ACTIVE" : "INACTIVE");
        }
        TableUtil.render(table);
    }

    private String categoryLabel(Product p, Map<Short, String> categoryNames) {
        if (p.getCategoryId() == null) {
            return "-";
        }
        return categoryNames.getOrDefault(p.getCategoryId(), "ID " + p.getCategoryId());
    }

    private String stockStatus(int stock) {
        if (stock == 0) return "OUT OF STOCK";
        if (stock <= 5) return "LOW";
        return "IN STOCK";
    }
}
