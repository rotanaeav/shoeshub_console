package kh.com.shoeshub.features.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateReportRequest(LocalDate startDate, LocalDate endDate) {
  public record Summary(long paidOrders, BigDecimal revenue) {}

  public record SalesRow(String name, long units, BigDecimal revenue) {}

  public record StockRow(String sku, String name, String size, String color, int stock) {}

  public record ReportData(
      CreateReportRequest period,
      Summary summary,
      List<SalesRow> daily,
      List<SalesRow> topProducts,
      List<SalesRow> categories,
      List<StockRow> lowStock) {}
}
