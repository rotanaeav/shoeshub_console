package kh.com.shoeshub.features.payment;

import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.utils.TableUtil;
import org.nocrala.tools.texttablefmt.Table;

import java.util.List;
import java.util.UUID;

/* UI class that only prints text to the console. Layer: View. */
public class PaymentUI {

    /* Displays the payment menu options. */
    public void displayPaymentMenu() {
        OutputUtil.printHeader("Payment Menu");
        OutputUtil.println(" [1] Pay for an Order");
        OutputUtil.println(" [2] View My Payments");
        OutputUtil.println(" [3] Export My Payments to CSV");
        OutputUtil.println(" [0] Back");
    }

    /* Renders the list of payments in a table. */
    public void displayPayments(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            OutputUtil.printInfo("No payments found.");
            return;
        }

        Table table = TableUtil.createTable(6, "Payment ID", "Order ID", "Method", "Amount", "Status", "Date");
        for (Payment p : payments) {
            table.addCell(p.getId().toString());
            table.addCell(p.getOrderId().toString());
            table.addCell(p.getMethod().name());
            table.addCell(p.getAmount().toString());
            table.addCell(p.getStatus().name());
            table.addCell(p.getCreatedAt().toString());
        }
        TableUtil.render(table);
    }

    /* Prints whether the payment succeeded or failed. */
    public void displayPaymentResult(Payment payment) {
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            OutputUtil.printSuccess("Payment successful for order: " + payment.getOrderId());
        } else {
            OutputUtil.printError("Payment failed for order: " + payment.getOrderId());
        }
    }

    /* Prints a fake KHQR reference. */
    public void displayKhqrReference() {
        // The reference is a simulation and is not stored.
        OutputUtil.printInfo("KHQR Reference: " + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        OutputUtil.printInfo("Scan to pay via KHQR");
    }
}
