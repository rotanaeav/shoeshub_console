package kh.com.shoeshub.config;

import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.authorize.Security;
import kh.com.shoeshub.features.auth.service.AuthService;
import kh.com.shoeshub.features.auth.service.AuthServiceImpl;
import kh.com.shoeshub.features.cart.CartController;
import kh.com.shoeshub.features.cart.repository.CartRepository;
import kh.com.shoeshub.features.cart.repository.CartRepositoryImpl;
import kh.com.shoeshub.features.cart.service.CartService;
import kh.com.shoeshub.features.cart.service.CartServiceImpl;
import kh.com.shoeshub.features.category.Category;
import kh.com.shoeshub.features.category.service.CategoryService;
import kh.com.shoeshub.features.product.ProductController;
import kh.com.shoeshub.features.product.repository.ProductVariantRepository;
import kh.com.shoeshub.features.product.repository.ProductVariantRepositoryImpl;
import kh.com.shoeshub.features.product.service.ProductService;
import kh.com.shoeshub.features.product.service.ProductServiceImpl;
import kh.com.shoeshub.features.user.UserController;
import kh.com.shoeshub.features.user.mapper.UserMapper;
import kh.com.shoeshub.features.user.repository.UserRepository;
import kh.com.shoeshub.features.user.repository.UserRepositoryImpl;
import kh.com.shoeshub.features.user.service.UserService;
import kh.com.shoeshub.features.user.service.UserServiceImpl;
import kh.com.shoeshub.features.user.service.UserValidator;
import kh.com.shoeshub.features.wishlist.WishlistController;
import kh.com.shoeshub.features.wishlist.repository.WishlistRepository;
import kh.com.shoeshub.features.wishlist.repository.WishlistRepositoryImpl;
import kh.com.shoeshub.features.wishlist.service.WishlistService;
import kh.com.shoeshub.features.wishlist.service.WishlistServiceImpl;
import kh.com.shoeshub.features.order.OrderController;
import kh.com.shoeshub.features.order.repository.OrderRepository;
import kh.com.shoeshub.features.order.repository.OrderRepositoryImpl;
import kh.com.shoeshub.features.order.service.OrderService;
import kh.com.shoeshub.features.order.service.OrderServiceImpl;
import kh.com.shoeshub.features.payment.PaymentController;

public class ServiceProvider {
    public static UserController getUserController(Security security) {
        UserRepository userRepository = new UserRepositoryImpl();
        UserMapper userMapper = new UserMapper();
        UserValidator userValidator = new UserValidator();
        UserService userService = new UserServiceImpl(userRepository, userMapper, userValidator);

        UserController userController = new UserController(userService, security);

        return userController;
    }

    public static CartController getCartController(Security security) {
        AuthorizationService authorizationService = new AuthorizationService(security);
        CartRepository cartRepository = new CartRepositoryImpl();
        ProductVariantRepository productVariantRepository = new ProductVariantRepositoryImpl();
        CartService cartService = new CartServiceImpl(cartRepository, productVariantRepository, authorizationService);
        ProductService productService = new ProductServiceImpl(authorizationService);
        CartController cartController = new CartController(cartService, productService);

        return cartController;
    }

    public static AuthService getAuthService() {
        UserRepository userRepository = new UserRepositoryImpl();
        AuthService authService = new AuthServiceImpl(userRepository);

        return authService;
    }

    public static AuthorizationService getAuthorizationService(
            Security security
    ) {
        return new AuthorizationService(security);
    }

    public static WishlistController getWishlistController(
            AuthorizationService authorizationService
    ) {

        WishlistRepository wishlistRepository =
                new WishlistRepositoryImpl();

        WishlistService wishlistService =
                new WishlistServiceImpl(
                        wishlistRepository,
                        authorizationService
                );

        ProductService productService = new ProductServiceImpl(authorizationService);
        return new WishlistController(wishlistService, productService);
    }
    public static CartService getCartService(Security security) {
        AuthorizationService authorizationService = new AuthorizationService(security);
        CartRepository cartRepository = new CartRepositoryImpl();
        ProductVariantRepository productVariantRepository = new ProductVariantRepositoryImpl();
        return new CartServiceImpl(cartRepository, productVariantRepository, authorizationService);
    }

    public static WishlistService getWishlistService(AuthorizationService authorizationService) {
        WishlistRepository wishlistRepository = new WishlistRepositoryImpl();
        return new WishlistServiceImpl(wishlistRepository, authorizationService);
    }

    public static ProductController getProductController(
            AuthorizationService authorizationService,
            CartService cartService,
            WishlistService wishlistService
    ) {
        return new ProductController(
                authorizationService,
                cartService,
                wishlistService
        );
    }

    public static ProductController getProductController(
            AuthorizationService authorizationService,
            WishlistController wishlistController,
            CartController cartController,
            UserController userController
    ) {
        return new ProductController(
                authorizationService,
                null,
                null
        );
    }

    public static OrderService getOrderService(AuthorizationService authorizationService) {
        OrderRepository orderRepository = new OrderRepositoryImpl();
        CartRepository cartRepository = new CartRepositoryImpl();
        ProductVariantRepository variantRepository = new ProductVariantRepositoryImpl();
        return new OrderServiceImpl(orderRepository, cartRepository, variantRepository, authorizationService);
    }

    public static OrderController getOrderController(AuthorizationService authorizationService) {
        OrderService orderService = getOrderService(authorizationService);
        return new OrderController(authorizationService, orderService);
    }

    public static OrderController getOrderController(AuthorizationService authorizationService, PaymentController paymentController) {
        OrderService orderService = getOrderService(authorizationService);
        return new OrderController(authorizationService, orderService, paymentController);
    }

    public static PaymentController getPaymentController(Security security) {
        return new PaymentController(security);
    }

    public static PaymentController getPaymentController(Security security, OrderService orderService) {
        return new PaymentController(security, orderService);
    }
}
