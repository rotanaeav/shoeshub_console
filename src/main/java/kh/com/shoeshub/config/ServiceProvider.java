package kh.com.shoeshub.config;

import kh.com.shoeshub.features.cart.CartController;
import kh.com.shoeshub.features.cart.repository.CartRepository;
import kh.com.shoeshub.features.cart.repository.CartRepositoryImpl;
import kh.com.shoeshub.features.cart.service.CartService;
import kh.com.shoeshub.features.cart.service.CartServiceImpl;
import kh.com.shoeshub.features.product.repository.ProductVariantRepository;
import kh.com.shoeshub.features.product.repository.ProductVariantRepositoryImpl;
import kh.com.shoeshub.features.user.UserController;
import kh.com.shoeshub.features.user.mapper.UserMapper;
import kh.com.shoeshub.features.user.repository.UserRepository;
import kh.com.shoeshub.features.user.repository.UserRepositoryImpl;
import kh.com.shoeshub.features.user.service.UserService;
import kh.com.shoeshub.features.user.service.UserServiceImpl;
import kh.com.shoeshub.features.user.service.UserValidator;

public class ServiceProvider {
    public static UserController getUserController() {
        UserRepository userRepository = new UserRepositoryImpl();
        UserMapper userMapper = new UserMapper();
        UserValidator userValidator = new UserValidator();
        UserService userService = new UserServiceImpl(userRepository, userMapper, userValidator);

        UserController userController = new UserController(userService);

        return userController;
    }

    public static CartController getCartController() {
        CartRepository cartRepository = new CartRepositoryImpl();
        ProductVariantRepository productVariantRepository = new ProductVariantRepositoryImpl();
        CartService cartService = new CartServiceImpl(cartRepository, productVariantRepository);
        CartController cartController = new CartController(cartService);

        return cartController;
    }
}
