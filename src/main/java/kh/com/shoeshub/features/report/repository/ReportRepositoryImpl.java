package kh.com.shoeshub.features.report.repository;

import kh.com.shoeshub.features.report.dto.CreateReportRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportRepositoryImpl implements ReportRepository {
    private static final String SALES = """
        FROM orders o JOIN payments pay ON pay.order_id=o.id
        WHERE o.status='PAID' AND pay.status='SUCCESS'
          AND NOT o.is_deleted AND NOT pay.is_deleted
          AND pay.created_at>=? AND pay.created_at<?
        """;
    @Override public boolean canViewReports(UUID user) {
        try (Connection c = DBConfig.get(); PreparedStatement ps=c.prepareStatement(
                "SELECT 1 FROM users WHERE id=? AND role IN ('ADMIN','SELLER') AND is_active AND NOT is_deleted")) {
            ps.setObject(1,user);
            try (ResultSet rs=ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw failure(e); }
    }
    private AppException failure(SQLException e) {
        return new AppException("Report database operation failed. Check the database connection.",e);
    }
    private void dates(PreparedStatement ps, CreateReportRequest r) throws SQLException {
        ps.setObject(1,r.fromInclusive()); ps.setObject(2,r.toExclusive());
    }
    @Override public CreateReportRequest.ReportData getReport(CreateReportRequest r) {
        try (Connection c = DBConfig.get()) {
            c.setReadOnly(true);
            c.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            c.setAutoCommit(false);
            // summaryy daily total and ranking share one consistent database snapshot
            try {
                CreateReportRequest.RevenueSummary summary;
                try (PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) AS orders, COALESCE(SUM(pay.amount),0) AS revenue "+SALES)) {
                    dates(ps,r);
                    try (ResultSet rs=ps.executeQuery()) {
                        rs.next(); summary=new CreateReportRequest.RevenueSummary(rs.getLong("orders"),rs.getBigDecimal("revenue"));
                    }
                }
                List<CreateReportRequest.SalesRow> sales=new ArrayList<>();
                try (PreparedStatement ps=c.prepareStatement("""
                        SELECT (pay.created_at AT TIME ZONE 'Asia/Phnom_Penh')::date AS sale_day,
                               COUNT(*) AS orders, COALESCE(SUM(pay.amount),0) AS revenue
                        """+SALES+" GROUP BY sale_day ORDER BY sale_day")) {
                    dates(ps,r);
                    try (ResultSet rs=ps.executeQuery()) {
                        while (rs.next()) sales.add(new CreateReportRequest.SalesRow(rs.getDate("sale_day").toLocalDate(),
                                rs.getLong("orders"),rs.getBigDecimal("revenue")));
                    }
                }
                List<CreateReportRequest.TopProductRow> top=new ArrayList<>();
                // avoid multiplying order lines by payment join use history unit_price
                String sql="""
                    SELECT p.id,p.name,SUM(oi.quantity) AS units,
                           SUM(oi.quantity * oi.unit_price) AS revenue
                    FROM order_items oi
                    JOIN orders o ON o.id=oi.order_id
                    JOIN product_variants v ON v.id=oi.variant_id
                    JOIN products p ON p.id=v.product_id
                    WHERE o.status='PAID' AND NOT o.is_deleted AND NOT oi.is_deleted
                      AND EXISTS (SELECT 1 FROM payments pay WHERE pay.order_id=o.id
                          AND pay.status='SUCCESS' AND NOT pay.is_deleted
                          AND pay.created_at>=? AND pay.created_at<?)
                    GROUP BY p.id,p.name ORDER BY units DESC,revenue DESC,p.id LIMIT ?
                    """;
                try (PreparedStatement ps=c.prepareStatement(sql)) {
                    dates(ps,r); ps.setInt(3,r.getTopLimit());
                    try (ResultSet rs=ps.executeQuery()) {
                        while (rs.next()) top.add(new CreateReportRequest.TopProductRow(rs.getObject("id",UUID.class),
                                rs.getString("name"),rs.getLong("units"),rs.getBigDecimal("revenue")));
                    }
                }
                c.commit(); return new CreateReportRequest.ReportData(summary,sales,top);
            } catch (SQLException | RuntimeException e) {
                try { c.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
                throw e;
            }
        } catch (SQLException e) {
            throw failure(e);
        }
    }
}
