package kh.com.shoeshub.features.payment;

import kh.com.shoeshub.authorize.Security;
import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.features.payment.dto.CreatePaymentRequest;
import kh.com.shoeshub.features.payment.export.PaymentCsvExporter;
import kh.com.shoeshub.features.payment.service.PaymentService;
import kh.com.shoeshub.features.payment.service.PaymentServiceImpl;
import kh.com.shoeshub.features.order.service.OrderService;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.utils.TableUtil;
import org.nocrala.tools.texttablefmt.Table;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class PaymentController {

    private final PaymentService paymentService = new PaymentServiceImpl();
    private final PaymentUI paymentUI = new PaymentUI();
    private final PaymentCsvExporter csvExporter = new PaymentCsvExporter();
    private final Security security;
    private OrderService orderService;

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public PaymentController(Security security, OrderService orderService) {
        this.security = security;
        this.orderService = orderService;
    }

    public PaymentController(Security security) {
        this(security, null);
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

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

    public void handleAdminPaymentMenu() {
        while (true) {
            paymentUI.displayAdminPaymentMenu();
            int choice = InputUtil.readInt("Select an option", 0, 3);
            switch (choice) {
                case 1 -> handleViewAllPayments();
                case 2 -> handleFilterPaymentsByStatus();
                case 3 -> handleExportAllPayments();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private UUID getCurrentCustomerId() {
        return security.getCurrentUser().id();
    }

    public void handlePayOrder(UUID customerId) {
        OutputUtil.printSubHeader("Pay for an Order");

        List<kh.com.shoeshub.features.order.Order> myOrders = orderService != null ? orderService.getMyOrders(customerId) : List.of();
        List<kh.com.shoeshub.features.order.Order> pendingOrders = myOrders.stream()
                .filter(o -> o.getStatus() == kh.com.shoeshub.features.order.OrderStatus.PENDING)
                .toList();

        if (pendingOrders.isEmpty()) {
            OutputUtil.printInfo("You have no pending orders waiting for payment.");
            InputUtil.pressEnter();
            return;
        }

        Table table = TableUtil.createTable(4, "#", "ORDER ID", "DATE", "AMOUNT ($)");
        for (int i = 0; i < pendingOrders.size(); i++) {
            kh.com.shoeshub.features.order.Order order = pendingOrders.get(i);
            String shortId = order.getId().toString().substring(0, 8) + "...";
            String date = order.getCreatedAt() != null ? order.getCreatedAt().toString().substring(0, 19) : "-";
            String amt = order.getTotalAmount() != null ? String.format("%.2f", order.getTotalAmount()) : "0.00";
            table.addCell(String.valueOf(i + 1));
            table.addCell(shortId);
            table.addCell(date);
            table.addCell(amt);
        }
        TableUtil.render(table);
        OutputUtil.println(" [0] Cancel / Back");

        int choice = InputUtil.readInt("Select order # to pay", 0, pendingOrders.size());
        if (choice == 0) {
            OutputUtil.printInfo("Payment cancelled.");
            return;
        }

        kh.com.shoeshub.features.order.Order selected = pendingOrders.get(choice - 1);
        processOrderPayment(selected.getId(), customerId);
        InputUtil.pressEnter();
    }

    public Payment processOrderPayment(UUID orderId, UUID customerId) {
        PaymentMethod method = InputUtil.readEnum("Payment Method", PaymentMethod.class);

        boolean isStaff = security != null && security.isAuthenticated()
                && (security.getCurrentUser().role() == kh.com.shoeshub.features.user.UserRole.ADMIN
                || security.getCurrentUser().role() == kh.com.shoeshub.features.user.UserRole.SELLER);

        boolean confirmed = false;
        if (method == PaymentMethod.CASH) {
            if (isStaff) {
                confirmed = InputUtil.readConfirm("Confirm cash received?");
            } else {
                confirmed = InputUtil.readConfirm("Pay with Cash ?");
            }
        } else if (method == PaymentMethod.KHQR) {
            paymentUI.displayKhqrReference();
            if (isStaff) {
                confirmed = InputUtil.readConfirm("Did the customer complete the payment?");
            } else {
                confirmed = InputUtil.readConfirm("Confirm payment via KHQR?");
            }
        }

        try {
            CreatePaymentRequest request = new CreatePaymentRequest(orderId, method);
            Payment result = paymentService.processPayment(request, customerId, confirmed);
            paymentUI.displayPaymentResult(result);
            return result;
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
            return null;
        }
    }

    private void handleViewMyPayments(UUID customerId) {
        OutputUtil.printSubHeader("My Payments");
        try {
            List<Payment> payments = paymentService.getTransactionHistory(customerId);
            paymentUI.displayPayments(payments);
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
        }
    }

    public void handleExportMyPayments(UUID customerId) {
        OutputUtil.printSubHeader("Export My Payments to CSV");
        try {
            List<Payment> payments = paymentService.getTransactionHistory(customerId);
            exportPayments(payments);
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
        }
    }

    public void handleExportAllPayments() {
        OutputUtil.printSubHeader("Export All Payments to CSV");
        try {
            List<Payment> payments = paymentService.getAllPayments();
            exportPayments(payments);
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
        }
    }

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

    public void handleViewAllPayments() {
        OutputUtil.printSubHeader("All Payments");
        try {
            List<Payment> payments = paymentService.getAllPayments();
            paymentUI.displayPayments(payments);
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
        }
    }

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
