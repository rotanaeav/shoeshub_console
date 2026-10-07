package kh.com.shoeshub.features.cart;

import kh.com.shoeshub.features.cart.dto.CartItemResponse;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class CartUI {

    private final CartController cartController;
    private final Scanner scanner;

    public CartUI(CartController cartController) {
        this.cartController = cartController;
        this.scanner = new Scanner(System.in);
    }

    public void showCartMenu(UUID userId) {

        while (true) {

            OutputUtil.printHeader("MY SHOPPING CART");

            OutputUtil.println("""
                    [1] View Cart
                    [2] Add To Cart
                    [3] Update Quantity
                    [4] Remove From Cart
                    [5] Clear Cart
                    [0] Back
                    """);

            int choice = InputUtil.readInt(
                    "Choose menu",
                    0,
                    5
            );

            try {

                switch (choice) {

                    case 1 -> viewCart(userId);

                    case 2 -> addToCart(userId);

                    case 3 -> updateQuantity(userId);

                    case 4 -> removeFromCart(userId);

                    case 5 -> clearCart(userId);

                    case 0 -> {
                        return;
                    }

                    default ->
                            OutputUtil.printError(
                                    "Invalid menu choice."
                            );
                }

            } catch (Exception e) {

                OutputUtil.printError(
                        e.getMessage()
                );
            }
        }
    }

    private void viewCart(UUID userId) {

        OutputUtil.printHeader("MY CART");

        List<CartItemResponse> cartItems =
                cartController.getMyCart(userId);

        if (cartItems.isEmpty()) {
            OutputUtil.println("Your cart is empty.");
            return;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (CartItemResponse item : cartItems) {

            OutputUtil.println("----------------------------");

            OutputUtil.println("Product : " + item.getProductName());
            OutputUtil.println("Size    : " + item.getSize());
            OutputUtil.println("Color   : " + item.getColor());
            OutputUtil.println("Price   : $" + item.getPrice());
            OutputUtil.println("Quantity: " + item.getQuantity());
            OutputUtil.println("Subtotal: $" + item.getSubtotal());

            total = total.add(item.getSubtotal());
        }

        OutputUtil.println("----------------------------");
        OutputUtil.println("Total   : $" + total);
    }

    private void addToCart(UUID userId) {

        System.out.println();
        System.out.println("========== ADD TO CART ==========");

        System.out.print("Enter Variant ID: ");
        UUID variantId = UUID.fromString(
                scanner.nextLine()
        );

        System.out.print("Enter Quantity: ");
        int quantity = Integer.parseInt(
                scanner.nextLine()
        );

        CartItem cartItem =
                cartController.addToCart(
                        userId,
                        variantId,
                        quantity
                );

        System.out.println();
        System.out.println(
                "Item added to cart successfully."
        );

        System.out.println(
                "Cart Item ID: " + cartItem.getId()
        );

        System.out.println(
                "Quantity: " + cartItem.getQuantity()
        );
    }

    private void updateQuantity(UUID userId) {

        System.out.println();
        System.out.println(
                "========== UPDATE QUANTITY =========="
        );

        System.out.print("Enter Cart Item ID: ");
        UUID cartItemId = UUID.fromString(
                scanner.nextLine()
        );

        System.out.print("Enter New Quantity: ");
        int quantity = Integer.parseInt(
                scanner.nextLine()
        );

        CartItem cartItem =
                cartController.updateQuantity(
                        userId,
                        cartItemId,
                        quantity
                );

        System.out.println();
        System.out.println(
                "Quantity updated successfully."
        );

        System.out.println(
                "New Quantity: "
                        + cartItem.getQuantity()
        );
    }

    private void removeFromCart(UUID userId) {

        System.out.println();
        System.out.println(
                "========== REMOVE FROM CART =========="
        );

        System.out.print("Enter Cart Item ID: ");
        UUID cartItemId = UUID.fromString(
                scanner.nextLine()
        );

        System.out.print(
                "Are you sure? (y/n): "
        );

        String confirmation =
                scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("y")) {

            System.out.println(
                    "Remove cancelled."
            );

            return;
        }

        cartController.removeFromCart(
                userId,
                cartItemId
        );

        System.out.println(
                "Item removed from cart successfully."
        );
    }

    private void clearCart(UUID userId) {

        System.out.println();
        System.out.println(
                "========== CLEAR CART =========="
        );

        System.out.print(
                "Are you sure you want to clear your cart? (y/n): "
        );

        String confirmation =
                scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("y")) {

            System.out.println(
                    "Clear cart cancelled."
            );

            return;
        }

        cartController.clearCart(userId);

        System.out.println(
                "Cart cleared successfully."
        );
    }
}
