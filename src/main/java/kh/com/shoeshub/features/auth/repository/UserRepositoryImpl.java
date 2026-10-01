package kh.com.shoeshub.features.auth.repository;

import kh.com.shoeshub.features.auth.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public User save(User entity) {
        // TODO: Implement JDBC insert
        return entity;
    }

    @Override
    public Optional<User> findById(UUID id) {
        // TODO: Implement JDBC query by id
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        // TODO: Implement JDBC query all
        return Collections.emptyList();
    }

    @Override
    public User update(UUID id, User entity) {
        // TODO: Implement JDBC update
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        // TODO: Implement JDBC soft delete
    }
}
