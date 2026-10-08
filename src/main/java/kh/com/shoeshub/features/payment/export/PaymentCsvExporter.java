package kh.com.shoeshub.features.payment.export;

import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.exception.ValidationException;
import kh.com.shoeshub.features.payment.Payment;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PaymentCsvExporter {

    private static final String HEADER = "Payment ID,Order ID,Method,Amount,Status,Date";

    public Path export(List<Payment> payments, Path target) {
        if (payments == null || target == null) {
            throw new ValidationException("Payments list and target path cannot be null");
        }
        try {
            Path parent = target.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
                writer.write(HEADER);
                writer.newLine();

                for (Payment p : payments) {
                    writer.write(escapeCsv(p.getId() == null ? null : p.getId().toString()));
                    writer.write(',');
                    writer.write(escapeCsv(p.getOrderId() == null ? null : p.getOrderId().toString()));
                    writer.write(',');
                    writer.write(escapeCsv(p.getMethod() == null ? null : p.getMethod().name()));
                    writer.write(',');
                    writer.write(escapeCsv(p.getAmount() == null ? null : p.getAmount().toString()));
                    writer.write(',');
                    writer.write(escapeCsv(p.getStatus() == null ? null : p.getStatus().name()));
                    writer.write(',');
                    writer.write(escapeCsv(p.getCreatedAt() == null ? null : p.getCreatedAt().toString()));
                    writer.newLine();
                }
            }

            return target;
        } catch (IOException e) {
            throw new AppException("Failed to export payments to CSV: " + e.getMessage(), e);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
