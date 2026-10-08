package kh.com.shoeshub.features.report.service;

import java.nio.file.Path;
import kh.com.shoeshub.features.report.dto.CreateReportRequest;
import kh.com.shoeshub.features.report.dto.CreateReportRequest.ReportData;

public interface ReportService {
  ReportData getRevenueReport(CreateReportRequest request);

  Path exportSalesReportToCsv(CreateReportRequest request);
}
