package kh.com.shoeshub.features.product.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.product.ProductVariant;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepository extends CrudRepository<ProductVariant, UUID> {
    List<ProductVariant> findByProductId(UUID productId);
    ProductVariant updateStock(UUID id, int newStock);
    boolean updateStockWithConnection(Connection conn, UUID id, int quantityToDeduct) throws SQLException;
    Optional<ProductVariant> findDeleted(UUID productId, BigDecimal size, String color);
    ProductVariant restore(UUID id, String color, int stockQuantity);
    boolean existsVariant(UUID productId, BigDecimal size, String color);
//    List<ProductVariant> findLowStock(int threshold);
}
