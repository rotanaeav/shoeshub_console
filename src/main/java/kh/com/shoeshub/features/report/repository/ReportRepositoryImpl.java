package kh.com.shoeshub.features.report.repository;

import java.sql.*;
import java.time.ZoneId;
import java.util.*;
import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.features.report.dto.CreateReportRequest;
import kh.com.shoeshub.features.report.dto.CreateReportRequest.*;

public class ReportRepositoryImpl implements ReportRepository {
  // Use payment time, with inclusive Cambodia calendar dates and an exclusive next-day bound.
  private static final String SALES =
      """
      FROM orders o JOIN payments pay ON pay.order_id=o.id
      WHERE NOT o.is_deleted AND NOT pay.is_deleted
      AND o.status='PAID' AND pay.status='SUCCESS'
      AND pay.created_at>=? AND pay.created_at<?
      """;
  private static final String ITEMS =
      """
      FROM order_items oi JOIN orders o ON o.id=oi.order_id
      JOIN payments pay ON pay.order_id=o.id
      JOIN product_variants v ON v.id=oi.variant_id
      JOIN products p ON p.id=v.product_id JOIN categories c ON c.id=p.category_id
      WHERE NOT oi.is_deleted AND NOT o.is_deleted AND NOT pay.is_deleted
      AND o.status='PAID' AND pay.status='SUCCESS'
      AND pay.created_at>=? AND pay.created_at<?
      """;

  private <T> List<T> query(Connection c, String sql, CreateReportRequest r, RowMapper<T> mapper)
      throws SQLException {
    try (PreparedStatement s = c.prepareStatement(sql)) {
      if (r != null) {
        var zone = ZoneId.of("Asia/Phnom_Penh");
        s.setTimestamp(1, Timestamp.from(r.startDate().atStartOfDay(zone).toInstant()));
        s.setTimestamp(2, Timestamp.from(r.endDate().plusDays(1).atStartOfDay(zone).toInstant()));
      }
      try (ResultSet rs = s.executeQuery()) {
        return mapper.mapRows(rs);
      }
    }
  }

  public ReportData getRevenueReport(CreateReportRequest r) {
    try (Connection c = DBConfig.get()) {
      // One read-only snapshot keeps totals and breakdowns consistent during checkout.
      c.setReadOnly(true);
      c.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
      c.setAutoCommit(false);
      try {
        var summary =
            query(
                    c,
                    "SELECT COUNT(*) AS count,COALESCE(SUM(pay.amount),0) AS revenue " + SALES,
                    r,
                    rs -> new Summary(rs.getLong("count"), rs.getBigDecimal("revenue")))
                .getFirst();
        RowMapper<SalesRow> mapper =
            rs ->
                new SalesRow(
                    rs.getString("name"), rs.getLong("units"), rs.getBigDecimal("revenue"));
        var daily =
            query(
                c,
                "SELECT (pay.created_at AT TIME ZONE 'Asia/Phnom_Penh')::date::text AS name,"
                    + " COUNT(*) AS units,SUM(pay.amount) AS revenue "
                    + SALES
                    + " GROUP BY 1 ORDER BY 1",
                r,
                mapper);
        var top =
            query(
                c,
                "SELECT p.sku || ' - ' || p.name AS name,SUM(oi.quantity) AS"
                    + " units,SUM(oi.quantity*oi.unit_price) AS revenue "
                    + ITEMS
                    + " GROUP BY p.id,p.sku,p.name ORDER BY units DESC,revenue DESC,p.id LIMIT 10",
                r,
                mapper);
        var categories =
            query(
                c,
                "SELECT c.name AS name,SUM(oi.quantity) AS units,SUM(oi.quantity*oi.unit_price) AS"
                    + " revenue "
                    + ITEMS
                    + " GROUP BY c.id,c.name ORDER BY revenue DESC,c.id",
                r,
                mapper);
        var stock =
            query(
                c,
                """
                SELECT p.sku,p.name,v.size::text,v.color,v.stock_quantity
                FROM product_variants v JOIN products p ON p.id=v.product_id
                WHERE NOT v.is_deleted AND NOT p.is_deleted AND p.is_active AND v.stock_quantity<=5
                ORDER BY v.stock_quantity,p.sku,v.size,v.color
                """,
                null,
                rs ->
                    new StockRow(
                        rs.getString("sku"),
                        rs.getString("name"),
                        rs.getString("size"),
                        rs.getString("color"),
                        rs.getInt("stock_quantity")));
        c.commit();
        return new ReportData(r, summary, daily, top, categories, stock);
      } catch (SQLException | RuntimeException e) {
        c.rollback();
        throw e;
      }
    } catch (SQLException e) {
      throw new AppException("Cannot generate report. Check the database connection.", e);
    }
  }
}
