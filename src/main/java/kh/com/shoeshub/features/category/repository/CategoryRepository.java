package kh.com.shoeshub.features.category.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.category.Category;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Short> {

    Optional<Category> findByName(String name);
    boolean hasProducts(Short categoryId);
}
