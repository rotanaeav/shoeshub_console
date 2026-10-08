package kh.com.shoeshub.features.cart;

import kh.com.shoeshub.features.cart.dto.CartItemResponse;
import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.product.ProductVariant;
import kh.com.shoeshub.utils.ColorUtil;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.utils.TableUtil;
import org.nocrala.tools.texttablefmt.Table;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import kh.com.shoeshub.features.order.OrderController;

public class CartUI {

    private final CartController cartController;
    private final OrderController orderController;

    public CartUI(CartController cartController, OrderController orderController) {
        this.cartController = cartController;
        this.orderController = orderController;
    }

    public CartUI(CartController cartController) {
        this(cartController, null);
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
                    [6] Proceed to Checkout
                    [0] Back
                    """);

            int choice = InputUtil.readInt("Choose menu", 0, 6);

            try {
                switch (choice) {
                    case 1 -> viewCart(userId);
                    case 2 -> addToCart(userId);
                    case 3 -> updateQuantity(userId);
                    case 4 -> removeFromCart(userId);
                    case 5 -> clearCart(userId);
                    case 6 -> checkout(userId);
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

    private void viewCart(UUID userId) {
        OutputUtil.printHeader("MY CART");

        List<CartItemResponse> cartItems = cartController.getMyCart(userId);

        if (cartItems.isEmpty()) {
            OutputUtil.printInfo("Your cart is empty.");
            InputUtil.pressEnter();
            return;
        }

        renderCartTable(cartItems);
        InputUtil.pressEnter();
    }

    private void checkout(UUID userId) {
        OutputUtil.printHeader("PROCEED TO CHECKOUT");

        List<CartItemResponse> cartItems = cartController.getMyCart(userId);
        if (cartItems.isEmpty()) {
            OutputUtil.printWarning("Your cart is empty. Please add items before checking out.");
            InputUtil.pressEnter();
            return;
        }

        renderCartTable(cartItems);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItemResponse item : cartItems) {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        OutputUtil.println("Total Items : " + cartItems.size());
        OutputUtil.println("Total Amount: " + ColorUtil.BOLD + "$" + String.format("%.2f", total) + ColorUtil.RESET);

        boolean confirm = InputUtil.readConfirm("Do you want to confirm checkout?");
        if (!confirm) {
            OutputUtil.println("Checkout cancelled.");
            return;
        }

        if (orderController != null) {
            orderController.handleCheckout(userId);
        } else {
            OutputUtil.printError("Order service is currently unavailable.");
        }
        InputUtil.pressEnter();
    }

    private void addToCart(UUID userId) {
        OutputUtil.printHeader("ADD TO CART");

        // Step 1: Select a Product
        List<Product> products = cartController.getActiveProducts();
        if (products.isEmpty()) {
            OutputUtil.printWarning("No products available to purchase.");
            return;
        }

        OutputUtil.printSubHeader("Step 1: Select a Product");
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
        int productChoice = InputUtil.readInt("Select product #", 0, products.size());
        if (productChoice == 0) {
            OutputUtil.println("Add to cart cancelled.");
            return;
        }

        Product selectedProduct = products.get(productChoice - 1);

        // Step 2: Select a Variant (Size & Color)
        List<ProductVariant> variants = cartController.getVariantsByProductId(selectedProduct.getId());
        if (variants.isEmpty()) {
            OutputUtil.printWarning("No variants available for " + selectedProduct.getName() + ".");
            return;
        }

        OutputUtil.printSubHeader("Step 2: Select Variant for " + selectedProduct.getName());
        Table variantTable = TableUtil.createTable(4, "#", "SIZE", "COLOR", "STOCK");
        for (int i = 0; i < variants.size(); i++) {
            ProductVariant v = variants.get(i);
            variantTable.addCell(String.valueOf(i + 1));
            variantTable.addCell(v.getSize() != null ? v.getSize().stripTrailingZeros().toPlainString() : "-");
            variantTable.addCell(v.getColor() != null ? v.getColor() : "-");
            variantTable.addCell(v.getStockQuantity() <= 0 ? "OUT OF STOCK" : String.valueOf(v.getStockQuantity()));
        }
        TableUtil.render(variantTable);

        OutputUtil.println("[0] Cancel");
        int variantChoice = InputUtil.readInt("Select variant #", 0, variants.size());
        if (variantChoice == 0) {
            OutputUtil.println("Add to cart cancelled.");
            return;
        }

        ProductVariant selectedVariant = variants.get(variantChoice - 1);
        if (selectedVariant.getStockQuantity() <= 0) {
            OutputUtil.printError("Sorry, this variant is out of stock.");
            return;
        }

        // Step 3: Enter Quantity
        OutputUtil.printSubHeader("Step 3: Enter Quantity");
        OutputUtil.printInfo("Available stock: " + selectedVariant.getStockQuantity());
        int quantity = InputUtil.readInt("Enter quantity", 1, selectedVariant.getStockQuantity());

        cartController.addToCart(userId, selectedVariant.getId(), quantity);
        OutputUtil.printSuccess(String.format("Added %d x %s (Size: %s, Color: %s) to cart!",
                quantity,
                selectedProduct.getName(),
                selectedVariant.getSize() != null ? selectedVariant.getSize().stripTrailingZeros().toPlainString() : "-",
                selectedVariant.getColor()));
    }

    private void updateQuantity(UUID userId) {
        OutputUtil.printHeader("UPDATE ITEM QUANTITY");

        List<CartItemResponse> cartItems = cartController.getMyCart(userId);
        if (cartItems.isEmpty()) {
            OutputUtil.printInfo("Your cart is empty.");
            return;
        }

        renderCartTable(cartItems);

        OutputUtil.println("[0] Cancel");
        int itemChoice = InputUtil.readInt("Select item # to update", 0, cartItems.size());
        if (itemChoice == 0) {
            OutputUtil.println("Update cancelled.");
            return;
        }

        CartItemResponse selectedItem = cartItems.get(itemChoice - 1);
        int newQuantity = InputUtil.readPositiveInt("Enter new quantity for " + selectedItem.getProductName());

        cartController.updateQuantity(userId, selectedItem.getCartItemId(), newQuantity);
        OutputUtil.printSuccess("Quantity updated successfully to " + newQuantity + ".");
    }

    private void removeFromCart(UUID userId) {
        OutputUtil.printHeader("REMOVE ITEM FROM CART");

        List<CartItemResponse> cartItems = cartController.getMyCart(userId);
        if (cartItems.isEmpty()) {
            OutputUtil.printInfo("Your cart is empty.");
            return;
        }

        renderCartTable(cartItems);

        OutputUtil.println("[0] Cancel");
        int itemChoice = InputUtil.readInt("Select item # to remove", 0, cartItems.size());
        if (itemChoice == 0) {
            OutputUtil.println("Remove cancelled.");
            return;
        }

        CartItemResponse selectedItem = cartItems.get(itemChoice - 1);
        boolean confirmed = InputUtil.readConfirm("Are you sure you want to remove '" + selectedItem.getProductName() +
                "' (" + selectedItem.getColor() + ", Size " + (selectedItem.getSize() != null ? selectedItem.getSize().stripTrailingZeros().toPlainString() : "-") + ")?");

        if (!confirmed) {
            OutputUtil.println("Remove cancelled.");
            return;
        }

        cartController.removeFromCart(userId, selectedItem.getCartItemId());
        OutputUtil.printSuccess("Item removed from cart successfully.");
    }

    private void clearCart(UUID userId) {
        OutputUtil.printHeader("CLEAR CART");

        List<CartItemResponse> cartItems = cartController.getMyCart(userId);
        if (cartItems.isEmpty()) {
            OutputUtil.printInfo("Your cart is already empty.");
            return;
        }

        boolean confirmed = InputUtil.readConfirm("Are you sure you want to clear all items from your cart?");
        if (!confirmed) {
            OutputUtil.println("Clear cart cancelled.");
            return;
        }

        cartController.clearCart(userId);
        OutputUtil.printSuccess("Cart cleared successfully.");
    }

    private void renderCartTable(List<CartItemResponse> cartItems) {
        Table table = TableUtil.createTable(7, "#", "PRODUCT", "SIZE", "COLOR", "PRICE ($)", "QTY", "SUBTOTAL ($)");

        BigDecimal grandTotal = BigDecimal.ZERO;
        for (int i = 0; i < cartItems.size(); i++) {
            CartItemResponse item = cartItems.get(i);
            table.addCell(String.valueOf(i + 1));
            table.addCell(item.getProductName());
            table.addCell(item.getSize() != null ? item.getSize().stripTrailingZeros().toPlainString() : "-");
            table.addCell(item.getColor() != null ? item.getColor() : "-");
            table.addCell(item.getPrice() != null ? String.format("%.2f", item.getPrice()) : "0.00");
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(item.getSubtotal() != null ? String.format("%.2f", item.getSubtotal()) : "0.00");

            if (item.getSubtotal() != null) {
                grandTotal = grandTotal.add(item.getSubtotal());
            }
        }
        TableUtil.render(table);
        OutputUtil.println(ColorUtil.BOLD + "Cart Total: $" + String.format("%.2f", grandTotal) + ColorUtil.RESET);
    }
}
