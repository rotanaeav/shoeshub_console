package kh.com.shoeshub.features.auth.service;

import kh.com.shoeshub.features.auth.repository.UserRepository;
import kh.com.shoeshub.features.auth.repository.UserRepositoryImpl;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository = new UserRepositoryImpl();

    // TODO: Implement user & authentication business logic
}
