package kh.com.shoeshub.features.product.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.exception.ValidationException;
import kh.com.shoeshub.features.category.Category;
import kh.com.shoeshub.features.category.repository.CategoryRepository;
import kh.com.shoeshub.features.category.repository.CategoryRepositoryImpl;
import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.product.repository.ProductRepository;
import kh.com.shoeshub.features.product.repository.ProductRepositoryImpl;
import kh.com.shoeshub.features.user.UserRole;

public class ProductCsvService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorizationService auth;

    public record ImportResult(int totalProcessed, int successCount, List<String> errors) {}

    public ProductCsvService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            AuthorizationService auth) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.auth = auth;
    }

    public ProductCsvService(AuthorizationService auth) {
        this(new ProductRepositoryImpl(), new CategoryRepositoryImpl(), auth);
    }

    /**
     * Export all active products to exports/products/products_<timestamp>.csv
     */
    public Path exportProductsToCsv() {
        auth.requireAnyRole(UserRole.ADMIN);

        try {
            Path exportDir = Path.of("exports", "products");
            Files.createDirectories(exportDir);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path target = exportDir.resolve("products_" + timestamp + ".csv");

            List<Product> products = productRepository.findAll();

            try (BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
                // Header
                writer.write("sku,name,description,price,category_id,is_active");
                writer.newLine();

                for (Product p : products) {
                    writer.write(sanitize(p.getSku()) + ",");
                    writer.write(sanitize(p.getName()) + ",");
                    writer.write(sanitize(p.getDescription()) + ",");
                    writer.write((p.getPrice() != null ? p.getPrice().toPlainString() : "0.00") + ",");
                    writer.write((p.getCategoryId() != null ? p.getCategoryId().toString() : "") + ",");
                    writer.write(Boolean.toString(p.isActive()));
                    writer.newLine();
                }
            }

            return target.toAbsolutePath();
        } catch (IOException e) {
            throw new AppException("Failed to export products to CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Import products from CSV file (Admin only)
     */
    public ImportResult importProductsFromCsv(Path csvPath) {
        auth.requireAnyRole(UserRole.ADMIN);

        if (csvPath == null || !Files.exists(csvPath)) {
            throw new ValidationException("CSV file not found at: " + csvPath);
        }

        // Cache existing valid categories
        Set<Short> validCategoryIds = new HashSet<>();
        for (Category c : categoryRepository.findAll()) {
            validCategoryIds.add(c.getId());
        }

        List<Product> toInsert = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int lineIndex = 0;

        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line;
            boolean isHeader = true;
            boolean hasSkuColumn = false;

            while ((line = reader.readLine()) != null) {
                lineIndex++;
                if (line.isBlank()) {
                    continue;
                }

                List<String> tokens = parseCsvLine(line);
                if (isHeader) {
                    isHeader = false;
                    String first = tokens.isEmpty() ? "" : tokens.get(0).toLowerCase();
                    if (first.equals("sku")) {
                        hasSkuColumn = true;
                        continue;
                    } else if (first.equals("name") || first.equals("product") || first.equals("product_name")) {
                        hasSkuColumn = false;
                        continue;
                    }
                }

                String sku;
                String name;
                String description;
                String priceStr;
                String catStr;

                if (hasSkuColumn) {
                    if (tokens.size() < 4) {
                        errors.add("Line " + lineIndex + ": Insufficient columns (expected: sku,name,description,price,category).");
                        continue;
                    }
                    String rawSku = tokens.get(0).trim();
                    sku = rawSku.isBlank() ? productRepository.generateNextSku() : rawSku;
                    name = tokens.size() > 1 ? tokens.get(1).trim() : "";
                    description = tokens.size() > 2 ? tokens.get(2).trim() : "";
                    priceStr = tokens.size() > 3 ? tokens.get(3).trim() : "";
                    catStr = tokens.size() > 4 ? tokens.get(4).trim() : "";

                    if (!rawSku.isBlank() && productRepository.findBySku(sku).isPresent()) {
                        errors.add("Line " + lineIndex + ": SKU '" + sku + "' already exists in database.");
                        continue;
                    }
                } else {
                    if (tokens.size() < 3) {
                        errors.add("Line " + lineIndex + ": Insufficient columns (expected: name,description,price,category).");
                        continue;
                    }
                    sku = productRepository.generateNextSku();
                    name = tokens.get(0).trim();
                    description = tokens.size() > 1 ? tokens.get(1).trim() : "";
                    priceStr = tokens.size() > 2 ? tokens.get(2).trim() : "";
                    catStr = tokens.size() > 3 ? tokens.get(3).trim() : "";
                }

                if (name.isBlank()) {
                    errors.add("Line " + lineIndex + ": Product name cannot be empty.");
                    continue;
                }

                BigDecimal price;
                try {
                    price = new BigDecimal(priceStr);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.add("Line " + lineIndex + ": Price must be greater than 0.");
                        continue;
                    }
                } catch (Exception e) {
                    errors.add("Line " + lineIndex + ": Invalid price format '" + priceStr + "'.");
                    continue;
                }

                Short categoryId = null;
                if (!catStr.isBlank()) {
                    try {
                        categoryId = Short.parseShort(catStr);
                        if (!validCategoryIds.contains(categoryId)) {
                            errors.add("Line " + lineIndex + ": Category ID " + categoryId + " does not exist.");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        // Allow category name (e.g. "Running", "Sneakers", "Formal")
                        Optional<Category> found = categoryRepository.findByName(catStr.trim());
                        if (found.isPresent()) {
                            categoryId = found.get().getId();
                        } else {
                            errors.add("Line " + lineIndex + ": Category '" + catStr + "' does not exist.");
                            continue;
                        }
                    }
                }

                Product p = Product.builder()
                        .id(UUID.randomUUID())
                        .sku(sku)
                        .name(name)
                        .description(description.isBlank() ? null : description)
                        .price(price)
                        .categoryId(categoryId)
                        .active(true)
                        .deleted(false)
                        .build();

                toInsert.add(p);
            }

        } catch (IOException e) {
            throw new AppException("Failed to read CSV file: " + e.getMessage(), e);
        }

        // Batch insert valid products in single transaction
        int successCount = 0;
        if (!toInsert.isEmpty()) {
            String sql = """
                    INSERT INTO products (id, category_id, sku, name, description, price, is_active)
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                    """;

            try (Connection conn = DBConfig.get()) {
                conn.setAutoCommit(false);
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (Product p : toInsert) {
                        stmt.setObject(1, p.getId());
                        if (p.getCategoryId() != null) {
                            stmt.setShort(2, p.getCategoryId());
                        } else {
                            stmt.setNull(2, java.sql.Types.SMALLINT);
                        }
                        stmt.setString(3, p.getSku());
                        stmt.setString(4, p.getName());
                        stmt.setString(5, p.getDescription());
                        stmt.setBigDecimal(6, p.getPrice());
                        stmt.setBoolean(7, p.isActive());
                        stmt.addBatch();
                    }
                    int[] results = stmt.executeBatch();
                    conn.commit();
                    successCount = results.length;
                } catch (SQLException e) {
                    conn.rollback();
                    throw new AppException("Database transaction failed during bulk insert: " + e.getMessage(), e);
                }
            } catch (SQLException e) {
                throw new AppException("Database connection failed during import: " + e.getMessage(), e);
            }
        }

        return new ImportResult(lineIndex > 0 ? lineIndex - 1 : 0, successCount, errors);
    }

    /**
     * Creates a sample CSV template file at imports/products_sample.csv if missing
     */
    public Path ensureSampleCsvExists() {
        try {
            Path importsDir = Path.of("imports");
            Files.createDirectories(importsDir);
            Path sampleFile = importsDir.resolve("products_sample.csv");

            if (!Files.exists(sampleFile)) {
                try (BufferedWriter writer = Files.newBufferedWriter(sampleFile, StandardCharsets.UTF_8)) {
                    writer.write("name,description,price,category");
                    writer.newLine();
                    writer.write("Nike Air Max 90,Classic running sneaker,135.00,1");
                    writer.newLine();
                    writer.write("Adidas Ultraboost Light,High performance running shoe,190.00,Running");
                    writer.newLine();
                    writer.write("Puma Suede Classic,Vintage lifestyle sneakers,75.50,Sneakers");
                    writer.newLine();
                }
            }
            return sampleFile;
        } catch (IOException e) {
            throw new AppException("Failed to create sample CSV: " + e.getMessage(), e);
        }
    }

    private String sanitize(Object val) {
        if (val == null) {
            return "\"\"";
        }
        String s = String.valueOf(val);
        // Neutralize spreadsheet formulas from text fields
        if (!s.isEmpty() && "=+-@\t\r\n".indexOf(s.charAt(0)) >= 0) {
            s = "'" + s;
        }
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private static List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }
}
