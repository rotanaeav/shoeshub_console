package kh.com.shoeshub.features.category.repository;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.exception.ValidationException;
import kh.com.shoeshub.features.category.Category;
import kh.com.shoeshub.features.category.mapper.CategoryMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CategoryRepositoryImpl implements CategoryRepository {

    private final RowMapper<Category> categoryMapper = new CategoryMapper();

    @Override
    public Category save(Category category) {
        String sql = """
                INSERT INTO categories (name, description)
                VALUES (?, ?);
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw toAppException("save category", e);
        }
        // name is unique, so we can load the new row (with its generated id) by name
        return findByName(category.getName())
                .orElseThrow(() -> new AppException("Category was saved but could not be loaded."));
    }

    @Override
    public Optional<Category> findById(Short id) {
        String sql = """
                SELECT * FROM categories
                WHERE id = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setShort(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(categoryMapper.mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw toAppException("find category", e);
        }
    }

    @Override
    public List<Category> findAll() {
        String sql = """
                SELECT * FROM categories
                WHERE is_deleted = FALSE
                ORDER BY id;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return categoryMapper.mapRows(rs);

        } catch (SQLException e) {
            throw toAppException("load categories", e);
        }
    }

    @Override
    public Category update(Short id, Category category) {
        String sql = """
                UPDATE categories
                SET name = ?, description = ?
                WHERE id = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setShort(3, id);

            if (stmt.executeUpdate() == 0) {
                throw new NotFoundException("Category not found: " + id);
            }

        } catch (SQLException e) {
            throw toAppException("update category", e);
        }
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }

    @Override
    public void deleteById(Short id) {
        String sql = """
                UPDATE categories
                SET is_deleted = TRUE
                WHERE id = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setShort(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new NotFoundException("Category not found: " + id);
            }

        } catch (SQLException e) {
            throw toAppException("delete category", e);
        }
    }

    @Override
    public Optional<Category> findByName(String name) {
        String sql = """
                SELECT * FROM categories
                WHERE LOWER(name) = LOWER(?) AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(categoryMapper.mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw toAppException("find category by name", e);
        }
    }

    @Override
    public boolean hasProducts(Short categoryId) {
        String sql = """
                SELECT 1 FROM products
                WHERE category_id = ? AND is_deleted = FALSE
                LIMIT 1;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setShort(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw toAppException("check products of category", e);
        }
    }

    private AppException toAppException(String action, SQLException e) {
        if ("23505".equals(e.getSQLState())) {
            return new ValidationException("Category name already exists.");
        }
        return new AppException("Failed to " + action + ": " + e.getMessage(), e);
    }
}