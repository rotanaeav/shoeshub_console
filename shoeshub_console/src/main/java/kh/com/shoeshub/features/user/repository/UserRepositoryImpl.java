package kh.com.shoeshub.features.user.repository;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.features.user.User;

import java.sql.*;
import java.util.*;

public class UserRepositoryImpl implements UserRepository {
    private final RowMapper<User> rowMapper = new UserRowMapper();

    @Override
    public User save(User entity) {

        String sql = """
                INSERT INTO users (
                    role,
                    full_name,
                    username,
                    password_hash,
                    phone,
                    date_of_birth,
                    gender,
                    address
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {

            ps.setString(1, entity.getRole().name());
            ps.setString(2, entity.getFullName());
            ps.setString(3, entity.getUsername());
            ps.setString(4, entity.getPasswordHash());
            ps.setString(5, entity.getPhone());
            ps.setDate(6, entity.getDateOfBirth());
            ps.setString(7, entity.getGender());
            ps.setString(8, entity.getAddress());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getObject(1, UUID.class));
                }
            }


            return entity;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {

        String sql = """
                SELECT
                    id,
                    role,
                    full_name,
                    username,
                    phone,
                    date_of_birth,
                    gender,
                    address
                FROM users
                WHERE id = ?
                AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setObject(1, id);


            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(rowMapper.mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT
                    id,
                    role,
                    full_name,
                    username,
                    phone,
                    date_of_birth,
                    gender,
                    address
                FROM users
                WHERE username = ?
                AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setObject(1, username);


            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(rowMapper.mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByIdIncludeDeleted(UUID id) {
        String sql = """
                SELECT
                    id,
                    role,
                    full_name,
                    username,
                    phone,
                    date_of_birth,
                    gender,
                    address
                FROM users
                WHERE id = ?
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(rowMapper.mapRow(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = """
                SELECT
                    id,
                    role,
                    full_name,
                    username,
                    phone,
                    date_of_birth,
                    gender,
                    address
                FROM users
                WHERE is_deleted = FALSE
                """;
        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            try (ResultSet rs = ps.executeQuery()) {
                return rowMapper.mapRows(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User update(UUID id, User entity) {
        String sql = """
                UPDATE users
                SET
                    full_name = ?,
                    username = ?,
                    phone = ?,
                    date_of_birth = ?,
                    gender = ?,
                    address = ?
                WHERE id = ?
                AND is_deleted = FALSE
                """;
        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, entity.getFullName());
            ps.setString(2, entity.getUsername());
            ps.setString(3, entity.getPhone());
            ps.setDate(4, entity.getDateOfBirth());
            ps.setString(5, entity.getGender());
            ps.setString(6, entity.getAddress());
            ps.setObject(7, id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return null;
            }

            return entity;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean softDelete(UUID id) {
        String sql = """
                UPDATE users 
                SET is_deleted = TRUE 
                WHERE id = ? 
                AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {

            ps.setObject(1, id);

            return ps.executeUpdate() > 0;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> restore(UUID id) {

        String sql = """
                UPDATE users
                SET is_deleted = FALSE
                WHERE id = ?
                AND is_deleted = TRUE
                RETURNING
                    id,
                    role,
                    full_name,
                    username,
                    phone,
                    date_of_birth,
                    gender,
                    address
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(rowMapper.mapRow(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(UUID id) {

        String sql = """
                DELETE FROM users
                WHERE id = ?
                AND is_deleted = TRUE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
