package kh.com.shoeshub.features.product;

import kh.com.shoeshub.features.product.dto.CreateProductRequest;
import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.utils.TableUtil;
import org.nocrala.tools.texttablefmt.Table;

import java.util.List;

public class ProductUI {

    public void displayProductMenu() {
        // TODO: Display Product management menu
    }

    public CreateProductRequest getProductInput() {
        // TODO: Read product input using InputUtil
        return null;
    }
    //customer view or guest
    public void displayProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            OutputUtil.printInfo("No products found in the catalog.");
            return;
        }

        OutputUtil.printSubHeader("PRODUCT LIST");

        // 5 Columns: SKU, NAME, PRICE, CATEGORY, STATUS
        Table table = TableUtil.createTable(5, "SKU", "NAME", "PRICE ($)", "CATEGORY", "STATUS");
        for (Product p : products) {
            table.addCell(p.getSku() != null ? p.getSku() : "-");
            table.addCell(p.getName() != null ? p.getName() : "-");
            table.addCell(p.getPrice() != null ? "$" + p.getPrice().toPlainString() : "$0.00");
            table.addCell(p.getCategoryId() != null ? String.valueOf(p.getCategoryId()) : "-");
            table.addCell(p.isActive() ? "ACTIVE" : "INACTIVE");
        }
        TableUtil.render(table);
    }

    /**
     * sample :
     * Admin & Seller View:
     * TODO: If Admin or Seller needs to view full 36-char UUIDs for database operations:
     * - text-table-formatter will automatically expand column width to fit full UUID string (p.getId().toString()).
     */
    public void displayProductsForAdmin(List<Product> products) {
        if (products == null || products.isEmpty()) {
            OutputUtil.printInfo("No products found in the catalog.");
            return;
        }

        OutputUtil.printSubHeader("ADMIN PRODUCT CATALOG (" + products.size() + " Items)");

        Table table = TableUtil.createTable(6, "UUID", "SKU", "NAME", "PRICE ($)", "CATEGORY", "STATUS");
        for (Product p : products) {
            table.addCell(p.getId() != null ? p.getId().toString() : "-");
            table.addCell(p.getSku() != null ? p.getSku() : "-");
            table.addCell(p.getName() != null ? p.getName() : "-");
            table.addCell(p.getPrice() != null ? "$" + p.getPrice().toPlainString() : "$0.00");
            table.addCell(p.getCategoryId() != null ? String.valueOf(p.getCategoryId()) : "-");
            table.addCell(p.isActive() ? "ACTIVE" : "INACTIVE");
        }
        TableUtil.render(table);
    }

    public void displayVariants(List<ProductVariant> variants) {
        // TODO: Render variants table (Size, Color, Stock Quantity) using TableUtil
    }
}
