package kh.com.shoeshub.features.category.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.category.Category;

public interface CategoryRepository extends CrudRepository<Category, Short> {
    // TODO: Define custom category queries (e.g. findByName)
}
