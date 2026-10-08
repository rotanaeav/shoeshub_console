package kh.com.shoeshub.features.report;

import kh.com.shoeshub.features.report.service.ReportService;
import kh.com.shoeshub.utils.*;

public class ReportController {
  private final ReportService service;
  private final ReportUI ui = new ReportUI();

  public ReportController(ReportService service) {
    this.service = service;
  }

  public void handleViewRevenue() {
    try {
      ui.displayReport(service.getRevenueReport(ui.readRequest()));
    } catch (RuntimeException e) {
      OutputUtil.printError(e.getMessage());
    }
  }

  public void showMenu() {
    while (true) {
      OutputUtil.printHeader("ADMIN REPORTS & ANALYTICS");
      OutputUtil.println(" [1] View sales report\n [2] Export sales CSV\n [0] Back");
      switch (InputUtil.readInt("Choose menu", 0, 2)) {
        case 0 -> {
          return;
        }
        case 1 -> handleViewRevenue();
        case 2 -> {
          try {
            OutputUtil.printSuccess(
                "CSV saved: " + service.exportSalesReportToCsv(ui.readRequest()));
          } catch (RuntimeException e) {
            OutputUtil.printError(e.getMessage());
          }
        }
      }
    }
  }
}
