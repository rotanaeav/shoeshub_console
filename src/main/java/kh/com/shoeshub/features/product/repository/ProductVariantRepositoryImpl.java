package kh.com.shoeshub.features.product.repository;

import kh.com.shoeshub.features.product.ProductVariant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductVariantRepositoryImpl implements ProductVariantRepository {

    @Override
    public ProductVariant save(ProductVariant variant) {
        // TODO: Implement JDBC variant insert
        return variant;
    }

    @Override
    public Optional<ProductVariant> findById(UUID id) {
        // TODO: Implement JDBC query variant by id
        return Optional.empty();
    }

    @Override
    public List<ProductVariant> findAll() {
        // TODO: Implement JDBC query all
        return Collections.emptyList();
    }

    @Override
    public ProductVariant update(UUID id, ProductVariant entity) {
        // TODO: Implement JDBC update
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        // TODO: Implement JDBC soft delete
    }

    @Override
    public List<ProductVariant> findByProductId(UUID productId) {
        // TODO: Implement JDBC query variants by product
        return Collections.emptyList();
    }

    @Override
    public ProductVariant updateStock(UUID id, int newStock) {
        // TODO: Implement JDBC update stock
        return null;
    }

    @Override
    public boolean updateStockWithConnection(Connection conn, UUID id, int quantityToDeduct) throws SQLException {
        // TODO: Implement JDBC stock deduction inside checkout transaction
        return true;
    }
}
