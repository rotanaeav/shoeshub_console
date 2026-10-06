package kh.com.shoeshub.features.user.mapper;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.user.User;
import kh.com.shoeshub.features.user.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs) throws SQLException {

        User user = new User();

        user.setId(rs.getObject("id", UUID.class));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setFullName(rs.getString("full_name"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setDateOfBirth(rs.getObject("date_of_birth", LocalDate.class));
        user.setGender(rs.getString("gender"));
        user.setAddress(rs.getString("address"));
        user.setDeleted(rs.getBoolean("is_deleted"));
        user.setCreatedAt(rs.getTimestamp("created_at"));

        return user;
    }
}