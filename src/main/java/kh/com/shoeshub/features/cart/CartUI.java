package kh.com.shoeshub.features.cart;

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

            System.out.println();
            System.out.println("========== CART ==========");
            System.out.println("1. View Cart");
            System.out.println("2. Add To Cart");
            System.out.println("3. Update Quantity");
            System.out.println("4. Remove From Cart");
            System.out.println("5. Clear Cart");
            System.out.println("0. Back");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine();

            try {

                switch (choice) {

                    case "1" -> viewCart(userId);

                    case "2" -> addToCart(userId);

                    case "3" -> updateQuantity(userId);

                    case "4" -> removeFromCart(userId);

                    case "5" -> clearCart(userId);

                    case "0" -> {
                        return;
                    }

                    default ->
                            System.out.println(
                                    "Invalid option."
                            );
                }

            } catch (Exception e) {

                System.out.println(
                        "Error: " + e.getMessage()
                );
            }
        }
    }

    private void viewCart(UUID userId) {

        System.out.println();
        System.out.println("========== MY CART ==========");

        List<CartItem> cartItems =
                cartController.getMyCart(userId);

        if (cartItems.isEmpty()) {

            System.out.println("Your cart is empty.");
            return;
        }

        for (CartItem item : cartItems) {

            System.out.println("----------------------------");
            System.out.println(
                    "Cart Item ID: " + item.getId()
            );

            System.out.println(
                    "Variant ID: " + item.getVariantId()
            );

            System.out.println(
                    "Quantity: " + item.getQuantity()
            );
        }

        System.out.println("----------------------------");
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
