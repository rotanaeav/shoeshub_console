package kh.com.shoeshub.features.auth.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.auth.User;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    // TODO: Define custom query methods (e.g. findByUsername)
}
