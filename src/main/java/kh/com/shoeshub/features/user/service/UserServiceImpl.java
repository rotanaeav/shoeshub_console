package kh.com.shoeshub.features.user.service;

import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.features.user.User;
import kh.com.shoeshub.features.user.UserRole;
import kh.com.shoeshub.features.user.dto.CreateUserRequest;
import kh.com.shoeshub.features.user.dto.UpdateUserRequest;
import kh.com.shoeshub.features.user.dto.UserResponse;
import kh.com.shoeshub.features.user.mapper.UserMapper;
import kh.com.shoeshub.features.user.repository.UserRepository;
import kh.com.shoeshub.utils.PasswordUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator validator;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, UserValidator validator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.validator = validator;
    }

    @Override
    public UserResponse saveUser(CreateUserRequest request) {

        validator.validateCreateUser(request);

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already existed");
        }

        User user = userMapper.toEntity(request);

        user.setPasswordHash(PasswordUtil.hashPassword(request.password()));
        user.setRole(UserRole.CUSTOMER);

        Timestamp sqlTimestamp = new Timestamp(System.currentTimeMillis());

        user.setCreatedAt(sqlTimestamp);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse findUserById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse findUserByUsername(String name) {
        return userMapper.toUserResponse(userRepository.findByUsername(name).orElseThrow(() -> {
            throw new NotFoundException("Username not found");
        }));
    }

    @Override
    public List<UserResponse> findAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {

        validator.validateUpdateUser(request);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userMapper.updateEntity(user, request);

        return userMapper.toUserResponse(userRepository.update(user.getId(), user));
    }

    @Override
    public boolean softDeleteUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.isDeleted()) {
            throw new IllegalArgumentException("User is already deleted");
        }

        return userRepository.softDelete(user.getId());
    }

    @Override
    public UserResponse restoreUser(UUID id) {

        User user = userRepository.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isDeleted()) {
            throw new IllegalArgumentException("User has not been deleted");
        }

        boolean restored = userRepository.restore(user.getId()).isPresent();

        if (!restored) {
            throw new RuntimeException("Failed to restore user");
        }

        return userMapper.toUserResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("User not found"))
        );
    }

    @Override
    public void permanentDeleteUser(UUID id) {
        User user = (userRepository.findByIdIncludeDeleted(id).orElseThrow(() -> {
            throw new NotFoundException("User not found");
        }));

        if (!user.isDeleted()) {
            throw new IllegalArgumentException(
                    "User must be soft deleted before permanent deletion"
            );
        }

        userRepository.deleteById(user.getId());
    }
}
