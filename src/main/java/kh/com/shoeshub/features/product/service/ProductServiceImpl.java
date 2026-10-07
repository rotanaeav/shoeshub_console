package kh.com.shoeshub.features.product.service;

import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.exception.ValidationException;
import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.product.ProductVariant;
import kh.com.shoeshub.features.product.dto.CreateProductRequest;
import kh.com.shoeshub.features.product.repository.ProductRepository;
import kh.com.shoeshub.features.product.repository.ProductRepositoryImpl;
import kh.com.shoeshub.features.product.repository.ProductVariantRepository;
import kh.com.shoeshub.features.product.repository.ProductVariantRepositoryImpl;
import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.features.user.UserRole;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {

    private static final BigDecimal MAX_PRICE = new BigDecimal("99999999.99");
    private static final BigDecimal MAX_SIZE = new BigDecimal("999.9");

    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final ProductVariantRepository variantRepository = new ProductVariantRepositoryImpl();

    private final AuthorizationService authorizationService;
    public ProductServiceImpl(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
    // ---------------------------------------------------------------- products

    @Override
    public Product createProduct(CreateProductRequest request) {
        requireStaff();
        validateProductRequest(request);

        Product product = Product.builder()
                .categoryId(request.getCategoryId())
                .sku(productRepository.generateNextSku())
                .name(request.getName().trim())
                .description(trimOrNull(request.getDescription()))
                .price(request.getPrice())
                .active(true)
                .build();

        return productRepository.save(product);
    }
    @Override
    public Product getProductById(UUID id) {
        if (id == null) {
            throw new ValidationException("Product id is required.");
        }
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    // Admin view: every product that is not deleted (active and inactive)
    @Override
    public List<Product> getAllProducts() {
        requireStaff();
        return productRepository.findAll();
    }

    // Customer view: only active products
    @Override
    public List<Product> getActiveProducts() {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .toList();
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new ValidationException("Search keyword cannot be empty.");
        }
        boolean staff = isStaff();
        return productRepository.searchByName(keyword.trim()).stream()
                .filter(p -> staff || p.isActive())
                .toList();
    }

    @Override
    public List<Product> getProductsByCategory(Short categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new ValidationException("Category id must be greater than 0.");
        }
        boolean staff = isStaff();
        return productRepository.findByCategoryId(categoryId).stream()
                .filter(p -> staff || p.isActive())
                .toList();
    }

    @Override
    public Product updateProduct(UUID id, CreateProductRequest request) {
        requireStaff();
        Product existing = getProductById(id);
        validateProductRequest(request);

        // SKU and active flag are kept; only the editable fields change
        existing.setCategoryId(request.getCategoryId());
        existing.setName(request.getName().trim());
        existing.setDescription(trimOrNull(request.getDescription()));
        existing.setPrice(request.getPrice());

        return productRepository.update(id, existing);
    }

    @Override
    public void deleteProduct(UUID id) {
        requireStaff();
        getProductById(id); // throws NotFoundException if missing

        // Hide its variants too, so they can never be shown or sold
        for (ProductVariant variant : variantRepository.findByProductId(id)) {
            variantRepository.deleteById(variant.getId());
        }
        productRepository.deleteById(id);
    }

    // ---------------------------------------------------------------- variants

    @Override
    public ProductVariant addVariant(ProductVariant variant) {
        requireStaff();
        if (variant == null) {
            throw new ValidationException("Variant data is required.");
        }
        getProductById(variant.getProductId()); // product must exist

        if (variant.getSize() == null
                || variant.getSize().compareTo(BigDecimal.ZERO) <= 0
                || variant.getSize().compareTo(MAX_SIZE) > 0
                || variant.getSize().scale() > 1) {
            throw new ValidationException("Size must be greater than 0 with at most 1 decimal place (e.g. 42 or 42.5).");
        }
        if (variant.getColor() == null || variant.getColor().isBlank()) {
            throw new ValidationException("Color is required.");
        }
        if (variant.getColor().trim().length() > 30) {
            throw new ValidationException("Color must be at most 30 characters.");
        }
        if (variant.getStockQuantity() == null || variant.getStockQuantity() < 0) {
            throw new ValidationException("Stock quantity cannot be negative.");
        }

        variant.setColor(variant.getColor().trim());

        if (variantRepository.existsVariant(variant.getProductId(), variant.getSize(), variant.getColor())) {
            throw new ValidationException("This product already has a variant with the same size and color.");
        }
        return variantRepository.save(variant);
    }

    @Override
    public List<ProductVariant> getVariantsByProductId(UUID productId) {
        getProductById(productId); // throws if the product is missing or deleted
        return variantRepository.findByProductId(productId);
    }

    // One query for all variants (used by the product list, so the screen stays fast)
    @Override
    public Map<UUID, List<ProductVariant>> getVariantsGroupedByProduct() {
        return variantRepository.findAll().stream()
                .collect(Collectors.groupingBy(ProductVariant::getProductId));
    }

    @Override
    public void updateStock(UUID variantId, int newStock) {
        requireStaff();
        if (variantId == null) {
            throw new ValidationException("Variant id is required.");
        }
        if (newStock < 0) {
            throw new ValidationException("Stock quantity cannot be negative.");
        }
        variantRepository.updateStock(variantId, newStock);
    }
    @Override
    public Product setActive(UUID id, boolean active) {
        requireStaff();
        Product existing = getProductById(id);
        existing.setActive(active);
        return productRepository.update(id, existing);
    }

    // ----------------------------------------------------------------- helpers

    private void validateProductRequest(CreateProductRequest request) {
        if (request == null) {
            throw new ValidationException("Product data is required.");
        }
        if (request.getCategoryId() == null || request.getCategoryId() <= 0) {
            throw new ValidationException("Category is required.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("Product name is required.");
        }
        if (request.getName().trim().length() > 150) {
            throw new ValidationException("Product name must be at most 150 characters.");
        }
        BigDecimal price = request.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be greater than 0.");
        }
        if (price.scale() > 2) {
            throw new ValidationException("Price can have at most 2 decimal places.");
        }
        if (price.compareTo(MAX_PRICE) > 0) {
            throw new ValidationException("Price is too large.");
        }
    }

    // Only ADMIN and SELLER may change products, variants and stock
    private void requireStaff() {
        authorizationService.requireAnyRole(UserRole.ADMIN, UserRole.SELLER);
    }

    // true if the current user is ADMIN or SELLER (used to decide who sees inactive products)
    // true if the current user is ADMIN or SELLER (decides who can see inactive products)
    private boolean isStaff() {
        try {
            requireStaff();
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }

    private String trimOrNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }


}