package kh.com.shoeshub.features.payment;

import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.features.payment.dto.CreatePaymentRequest;
import kh.com.shoeshub.features.payment.export.PaymentCsvExporter;
import kh.com.shoeshub.features.payment.service.PaymentService;
import kh.com.shoeshub.features.payment.service.PaymentServiceImpl;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/** Handles user input and delegates to the service. Layer: Controller. */
public class PaymentController {

    private final PaymentService paymentService = new PaymentServiceImpl();
    private final PaymentUI paymentUI = new PaymentUI();
    private final PaymentCsvExporter csvExporter = new PaymentCsvExporter();

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /** Shows the payment menu loop. */
    public void handleViewTransactionHistory() {
        while (true) {
            paymentUI.displayPaymentMenu();
            int choice = InputUtil.readInt("Select an option", 0, 3);
            switch (choice) {
                case 1 -> handlePayOrder(getCurrentCustomerId());
                case 2 -> handleViewMyPayments(getCurrentCustomerId());
                case 3 -> handleExportMyPayments(getCurrentCustomerId());
                case 0 -> {
                    return;
                }
            }
        }
    }

    // TODO: replace with session when Auth is done

    // Returns a fixed test customer until Auth is merged.
    private UUID getCurrentCustomerId() {
        return UUID.fromString("c0000000-0000-0000-0000-000000000001");
    }

    /** Prompts for payment details and processes the payment. */
    private void handlePayOrder(UUID customerId) {
        OutputUtil.printSubHeader("Pay for an Order");
        UUID orderId;
        while (true) {
            String input = InputUtil.readRequiredText("Enter Order ID (0 to cancel)");
            if ("0".equals(input)) {
                OutputUtil.printInfo("Cancelled.");
                return;
            }
            try {
                orderId = UUID.fromString(input);
                break;
            } catch (IllegalArgumentException e) {
                OutputUtil.printError("Invalid Order ID. Type 0 to cancel.");
            }
        }
        PaymentMethod method = InputUtil.readEnum("Payment Method", PaymentMethod.class);

        // The y/n question comes BEFORE the service call so the database connection is
        // not held open while waiting for the user.
        boolean confirmed = false;
        if (method == PaymentMethod.CASH) {
            confirmed = InputUtil.readConfirm("Confirm cash received?");
        } else if (method == PaymentMethod.KHQR) {
            paymentUI.displayKhqrReference();
            confirmed = InputUtil.readConfirm("Did the customer pay?");
        }

        try {
            CreatePaymentRequest request = new CreatePaymentRequest(orderId, method);
            Payment result = paymentService.processPayment(request, customerId, confirmed);
            paymentUI.displayPaymentResult(result);
        } catch (AppException e) {
            // catch(AppException) keeps the menu from crashing and handles all
            // ValidationException/NotFoundException.
            OutputUtil.printError(e.getMessage());
        }
    }

    /** Shows payments for the current customer. */
    private void handleViewMyPayments(UUID customerId) {
        OutputUtil.printSubHeader("My Payments");
        try {
            List<Payment> payments = paymentService.getTransactionHistory(customerId);
            paymentUI.displayPayments(payments);
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
        }
    }

    /** Exports the customer's payments to CSV. */
    public void handleExportMyPayments(UUID customerId) {
        OutputUtil.printSubHeader("Export My Payments to CSV");
        try {
            List<Payment> payments = paymentService.getTransactionHistory(customerId);
            exportPayments(payments);
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
        }
    }

    /** Exports all payments to CSV for admins. */
    public void handleExportAllPayments() {
        OutputUtil.printSubHeader("Export All Payments to CSV");
        try {
            List<Payment> payments = paymentService.getAllPayments();
            exportPayments(payments);
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
        }
    }

    /** Helper to write payments to CSV. */
    private void exportPayments(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            OutputUtil.printWarning("No payments to export.");
            return;
        }
        String filename = "payments_" + LocalDateTime.now().format(FILE_TIMESTAMP) + ".csv";
        Path target = Path.of("exports", filename);
        Path result = csvExporter.export(payments, target);
        OutputUtil.printSuccess("Exported " + payments.size() + " payment(s) to: " + result.toAbsolutePath());
    }

    /** Shows all payments for admins. */
    public void handleViewAllPayments() {
        OutputUtil.printSubHeader("All Payments");
        try {
            List<Payment> payments = paymentService.getAllPayments();
            paymentUI.displayPayments(payments);
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
        }
    }

    /** Shows payments filtered by status for admins. */
    public void handleFilterPaymentsByStatus() {
        OutputUtil.printSubHeader("Filter Payments by Status");
        PaymentStatus status = InputUtil.readEnum("Payment Status", PaymentStatus.class);
        try {
            List<Payment> payments = paymentService.getPaymentsByStatus(status);
            paymentUI.displayPayments(payments);
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
        }
    }
}
