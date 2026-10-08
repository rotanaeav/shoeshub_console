package kh.com.shoeshub.features.user.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByIdIncludeDeleted(UUID id);

    // List<User> listDeletedUsers(User user);

    boolean softDelete(UUID id);

    Optional<User> restore(UUID id);
}
