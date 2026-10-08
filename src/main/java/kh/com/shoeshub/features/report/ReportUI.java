package kh.com.shoeshub.features.report;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import kh.com.shoeshub.features.report.dto.CreateReportRequest;
import kh.com.shoeshub.features.report.dto.CreateReportRequest.*;
import kh.com.shoeshub.utils.*;

public class ReportUI {
  private LocalDate date(String prompt) {
    while (true) {
      try {
        return LocalDate.parse(InputUtil.readRequiredText(prompt + " (YYYY-MM-DD)"));
      } catch (DateTimeParseException e) {
        OutputUtil.printError("Use a valid date such as 2026-10-08.");
      }
    }
  }

  public CreateReportRequest readRequest() {
    return new CreateReportRequest(date("Start date"), date("End date"));
  }

  private void sales(String title, List<SalesRow> rows, String countHeader) {
    OutputUtil.printSubHeader(title);
    if (rows.isEmpty()) {
      OutputUtil.printInfo("No sales in this period.");
      return;
    }
    var t = TableUtil.createTable(3, "Name / date", countHeader, "Revenue");
    rows.forEach(
        r -> {
          t.addCell(r.name());
          t.addCell(Long.toString(r.units()));
          t.addCell(r.revenue().toPlainString());
        });
    TableUtil.render(t);
  }

  public void displayReport(ReportData d) {
    OutputUtil.printHeader(
        "SALES REPORT " + d.period().startDate() + " to " + d.period().endDate());
    OutputUtil.printInfo(
        "Dates use Cambodia time. Revenue includes successful payments on paid orders.");
    OutputUtil.println(
        "Paid orders: "
            + d.summary().paidOrders()
            + " | Revenue: "
            + d.summary().revenue().toPlainString());
    sales("DAILY REVENUE", d.daily(), "Orders");
    sales("TOP 10 PRODUCTS", d.topProducts(), "Units");
    sales("REVENUE BY CATEGORY", d.categories(), "Units");
    OutputUtil.printSubHeader("CURRENT LOW STOCK (<=5, not filtered by sales dates)");
    if (d.lowStock().isEmpty()) {
      OutputUtil.printInfo("No low-stock variants.");
      return;
    }
    var t = TableUtil.createTable(5, "SKU", "Product", "Size", "Color", "Stock");
    d.lowStock()
        .forEach(
            r -> {
              t.addCell(r.sku());
              t.addCell(r.name());
              t.addCell(r.size());
              t.addCell(r.color());
              t.addCell(Integer.toString(r.stock()));
            });
    TableUtil.render(t);
  }
}
