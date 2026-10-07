package kh.com.shoeshub.features.wishlist;

import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.wishlist.mapper.WishlistResponse;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.utils.TableUtil;
import org.nocrala.tools.texttablefmt.Table;

import java.util.List;

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
                    default -> OutputUtil.printError("Invalid menu choice.");
                }
            } catch (Exception e) {
                OutputUtil.printError(e.getMessage() != null ? e.getMessage() : "An error occurred.");
            }
        }
    }

    private void viewWishlist() {
        OutputUtil.printHeader("MY WISHLIST");

        List<WishlistResponse> wishlists = wishlistController.getMyWishlist();

        if (wishlists.isEmpty()) {
            OutputUtil.printInfo("Your wishlist is empty.");
            return;
        }

        renderWishlistTable(wishlists);
    }

    private void addToWishlist() {
        OutputUtil.printHeader("ADD TO WISHLIST");

        List<Product> products = wishlistController.getActiveProducts();
        if (products.isEmpty()) {
            OutputUtil.printWarning("No products available to add to wishlist.");
            return;
        }

        OutputUtil.printSubHeader("Select a Product");
        Table productTable = TableUtil.createTable(4, "#", "SKU", "PRODUCT NAME", "PRICE ($)");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            productTable.addCell(String.valueOf(i + 1));
            productTable.addCell(p.getSku() != null ? p.getSku() : "-");
            productTable.addCell(p.getName());
            productTable.addCell(p.getPrice() != null ? String.format("%.2f", p.getPrice()) : "0.00");
        }
        TableUtil.render(productTable);

        OutputUtil.println("[0] Cancel");
        int choice = InputUtil.readInt("Select product #", 0, products.size());
        if (choice == 0) {
            OutputUtil.println("Add to wishlist cancelled.");
            return;
        }

        Product selected = products.get(choice - 1);
        wishlistController.addToWishlist(selected.getId());
        OutputUtil.printSuccess("Added '" + selected.getName() + "' to your wishlist successfully.");
    }

    private void removeFromWishlist() {
        OutputUtil.printHeader("REMOVE FROM WISHLIST");

        List<WishlistResponse> wishlists = wishlistController.getMyWishlist();
        if (wishlists.isEmpty()) {
            OutputUtil.printInfo("Your wishlist is empty.");
            return;
        }

        renderWishlistTable(wishlists);

        OutputUtil.println("[0] Cancel");
        int choice = InputUtil.readInt("Select item # to remove", 0, wishlists.size());
        if (choice == 0) {
            OutputUtil.println("Remove cancelled.");
            return;
        }

        WishlistResponse selected = wishlists.get(choice - 1);
        boolean confirmed = InputUtil.readConfirm("Are you sure you want to remove '" + selected.getProductName() + "' from your wishlist?");
        if (!confirmed) {
            OutputUtil.println("Remove cancelled.");
            return;
        }

        wishlistController.removeFromWishlist(selected.getProductId());
        OutputUtil.printSuccess("'" + selected.getProductName() + "' removed from wishlist successfully.");
    }

    private void renderWishlistTable(List<WishlistResponse> wishlists) {
        Table table = TableUtil.createTable(4, "#", "SKU", "PRODUCT", "PRICE ($)");

        for (int i = 0; i < wishlists.size(); i++) {
            WishlistResponse item = wishlists.get(i);
            table.addCell(String.valueOf(i + 1));
            table.addCell(item.getSku() != null ? item.getSku() : "-");
            table.addCell(item.getProductName());
            table.addCell(item.getPrice() != null ? String.format("%.2f", item.getPrice()) : "0.00");
        }

        TableUtil.render(table);
    }
}