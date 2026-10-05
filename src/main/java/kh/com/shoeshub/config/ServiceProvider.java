package kh.com.shoeshub.config;

import kh.com.shoeshub.features.user.UserController;
import kh.com.shoeshub.features.user.mapper.UserMapper;
import kh.com.shoeshub.features.user.repository.UserRepositoryImpl;
import kh.com.shoeshub.features.user.service.UserServiceImpl;
import kh.com.shoeshub.features.user.service.UserValidator;

public class ServiceProvider {
    public static UserController getUserController() {
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        UserMapper userMapper = new UserMapper();
        UserValidator userValidator = new UserValidator();
        UserServiceImpl userService = new UserServiceImpl(userRepository, userMapper, userValidator);

        UserController userController = new UserController(userService);

        return userController;
    }
}
