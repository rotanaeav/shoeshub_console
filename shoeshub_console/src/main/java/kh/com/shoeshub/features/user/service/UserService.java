package kh.com.shoeshub.features.user.service;

import kh.com.shoeshub.features.user.dto.CreateUserRequest;
import kh.com.shoeshub.features.user.dto.UpdateUserRequest;
import kh.com.shoeshub.features.user.dto.UserResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponse saveUser(CreateUserRequest request);

    UserResponse findUserById(UUID id);

    UserResponse findUserByUsername(String name);

    List<UserResponse> findAllUsers();

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    boolean softDeleteUser(UUID id);

    UserResponse restoreUser(UUID id);

    void permanentDeleteUser(UUID id);
}
