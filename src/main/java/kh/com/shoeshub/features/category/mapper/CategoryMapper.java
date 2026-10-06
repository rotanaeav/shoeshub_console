package kh.com.shoeshub.features.category.mapper;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.category.Category;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryMapper implements RowMapper<Category> {

    @Override
    public Category mapRow(ResultSet rs) throws SQLException {
        return Category.builder()
                .id(rs.getShort("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .deleted(rs.getBoolean("is_deleted"))
                .build();
    }
}
