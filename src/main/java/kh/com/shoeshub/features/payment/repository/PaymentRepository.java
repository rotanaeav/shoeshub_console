package kh.com.shoeshub.features.payment.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.payment.Payment;

import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, UUID> {
    // TODO: Define custom payment queries (e.g. findByOrderId, updateStatus)
}
