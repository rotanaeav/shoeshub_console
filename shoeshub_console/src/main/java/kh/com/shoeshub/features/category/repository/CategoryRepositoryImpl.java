package kh.com.shoeshub.features.category.repository;

import kh.com.shoeshub.features.category.Category;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CategoryRepositoryImpl implements CategoryRepository {

    @Override
    public Category save(Category entity) {
        // TODO: Implement JDBC insert
        return entity;
    }

    @Override
    public Optional<Category> findById(Short id) {
        // TODO: Implement JDBC query by id
        return Optional.empty();
    }

    @Override
    public List<Category> findAll() {
        // TODO: Implement JDBC query all
        return Collections.emptyList();
    }

    @Override
    public Category update(Short id, Category entity) {
        // TODO: Implement JDBC update
        return entity;
    }

    @Override
    public void deleteById(Short id) {
        // TODO: Implement JDBC soft delete
    }
}
