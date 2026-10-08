package kh.com.shoeshub.features.report.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.exception.*;
import kh.com.shoeshub.features.report.dto.CreateReportRequest;
import kh.com.shoeshub.features.report.dto.CreateReportRequest.*;
import kh.com.shoeshub.features.report.repository.ReportRepository;
import kh.com.shoeshub.features.user.UserRole;

public class ReportServiceImpl implements ReportService {
  private final ReportRepository repository;
  private final AuthorizationService auth;

  public ReportServiceImpl(ReportRepository repository, AuthorizationService auth) {
    this.repository = repository;
    this.auth = auth;
  }

  public ReportData getRevenueReport(CreateReportRequest r) {
    auth.requireAnyRole(UserRole.ADMIN);
    if (r == null || r.startDate() == null || r.endDate() == null)
      throw new ValidationException("Both dates are required.");
    if (r.startDate().isAfter(r.endDate()) || r.endDate().equals(java.time.LocalDate.MAX))
      throw new ValidationException("Invalid date range.");
    return repository.getRevenueReport(r);
  }

  private String csv(Object value) {
    String s = String.valueOf(value);
    // Neutralize spreadsheet formulas from product/category names.
    if (!s.isEmpty() && "=+-@\t\r\n".indexOf(s.charAt(0)) >= 0) s = "'" + s;
    return "\"" + s.replace("\"", "\"\"") + "\"";
  }

  private void row(BufferedWriter w, Object... values) throws IOException {
    for (int i = 0; i < values.length; i++) {
      if (i > 0) w.write(',');
      w.write(csv(values[i]));
    }
    w.newLine();
  }

  public Path exportSalesReportToCsv(CreateReportRequest r) {
    var data = getRevenueReport(r);
    try {
      var directory = Path.of("exports");
      Files.createDirectories(directory);
      var file =
          Files.createTempFile(
              directory, "sales-" + r.startDate() + "-" + r.endDate() + "-", ".csv");
      try (var w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
        row(w, "Period", r.startDate(), r.endDate());
        row(w, "Timezone", "Asia/Phnom_Penh");
        row(w, "Paid orders", data.summary().paidOrders());
        row(w, "Revenue", data.summary().revenue());
        row(w, "Section", "Name / date", "Units / orders", "Revenue");
        for (var x : data.daily()) row(w, "Daily", x.name(), x.units(), x.revenue());
        for (var x : data.topProducts()) row(w, "Top product", x.name(), x.units(), x.revenue());
        for (var x : data.categories()) row(w, "Category", x.name(), x.units(), x.revenue());
        row(
            w,
            "Current low stock (outside date filter)",
            "SKU",
            "Product",
            "Size",
            "Color",
            "Stock");
        for (var x : data.lowStock())
          row(w, "Low stock", x.sku(), x.name(), x.size(), x.color(), x.stock());
      }
      return file.toAbsolutePath();
    } catch (IOException e) {
      throw new AppException("Cannot export CSV.", e);
    }
  }
}
