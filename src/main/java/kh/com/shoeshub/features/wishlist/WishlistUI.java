package kh.com.shoeshub.features.wishlist;

import kh.com.shoeshub.features.wishlist.mapper.WishlistResponse;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;

import java.util.List;
import java.util.UUID;

public class WishlistUI {

    private final WishlistController wishlistController;

    public WishlistUI(WishlistController wishlistController) {
        this.wishlistController = wishlistController;
    }

    public void showWishlistMenu() {

        while (true) {

            OutputUtil.printHeader("MY WISHLIST");

            OutputUtil.println("""
                [1] View Wishlist
                [2] Add To Wishlist
                [3] Remove From Wishlist
                [0] Back
                """);

            int choice = InputUtil.readInt("Choose menu", 0, 3);

            try {
                switch (choice) {
                    case 1 -> viewWishlist();
                    case 2 -> addToWishlist();
                    case 3 -> removeFromWishlist();
                    case 0 -> {
                        return;
                    }
                    default -> OutputUtil.printError(
                            "Invalid menu choice."
                    );
                }
            } catch (Exception e) {
                OutputUtil.printError(e.getMessage());
            }
        }
    }

    private void viewWishlist() {

        OutputUtil.printHeader("MY WISHLIST");

        List<WishlistResponse> wishlists =
                wishlistController.getMyWishlist();

        if (wishlists.isEmpty()) {
            OutputUtil.println("Your wishlist is empty.");
            return;
        }

        int number = 1;

        for (WishlistResponse item : wishlists) {

            OutputUtil.println("----------------------------");
            OutputUtil.println("[" + number + "]");
            OutputUtil.println("Product     : " + item.getProductName());
            OutputUtil.println("SKU         : " + item.getSku());
            OutputUtil.println("Price       : $" + item.getPrice());

            if (item.getDescription() != null
                    && !item.getDescription().isBlank()) {

                OutputUtil.println(
                        "Description : " + item.getDescription()
                );
            }

            number++;
        }

        OutputUtil.println("----------------------------");
    }

    private void addToWishlist() {

        OutputUtil.printHeader("ADD TO WISHLIST");

        UUID productId = InputUtil.readUUID(
                "Enter Product ID"
        );

        wishlistController.addToWishlist(productId);

        OutputUtil.printSuccess(
                "Product added to wishlist successfully."
        );
    }

    private void removeFromWishlist() {

        OutputUtil.printHeader("REMOVE FROM WISHLIST");

        UUID productId = InputUtil.readUUID(
                "Enter Product ID"
        );

        String confirmation =
                InputUtil.readText("Are you sure? (y/n)");

        if (!confirmation.equalsIgnoreCase("y")) {
            OutputUtil.println("Remove cancelled.");
            return;
        }

        wishlistController.removeFromWishlist(productId);

        OutputUtil.printSuccess(
                "Product removed from wishlist successfully."
        );
    }
}