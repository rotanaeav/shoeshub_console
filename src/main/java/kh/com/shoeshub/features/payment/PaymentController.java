package kh.com.shoeshub.features.payment;

import kh.com.shoeshub.features.payment.service.PaymentService;
import kh.com.shoeshub.features.payment.service.PaymentServiceImpl;

public class PaymentController {

    private final PaymentService paymentService = new PaymentServiceImpl();
    private final PaymentUI paymentUI = new PaymentUI();

    public void handleViewTransactionHistory() {
        // TODO: Implement view transaction history
    }
}
