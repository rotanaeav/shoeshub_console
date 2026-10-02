package kh.com.shoeshub.features.payment.repository;

import kh.com.shoeshub.features.payment.Payment;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PaymentRepositoryImpl implements PaymentRepository {

    @Override
    public Payment save(Payment entity) {
        // TODO: Implement JDBC insert
        return entity;
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        // TODO: Implement JDBC query by id
        return Optional.empty();
    }

    @Override
    public List<Payment> findAll() {
        // TODO: Implement JDBC query all
        return Collections.emptyList();
    }

    @Override
    public Payment update(UUID id, Payment entity) {
        // TODO: Implement JDBC update
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        // TODO: Implement JDBC soft delete
    }
}
