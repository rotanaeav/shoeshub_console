package kh.com.shoeshub.features.payment.service;

import kh.com.shoeshub.features.payment.repository.PaymentRepository;
import kh.com.shoeshub.features.payment.repository.PaymentRepositoryImpl;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository = new PaymentRepositoryImpl();

    // TODO: Implement payment business logic
}
