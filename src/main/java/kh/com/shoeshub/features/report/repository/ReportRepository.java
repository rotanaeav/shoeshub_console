package kh.com.shoeshub.features.report.repository;

import kh.com.shoeshub.features.report.dto.CreateReportRequest;
import kh.com.shoeshub.features.report.dto.CreateReportRequest.ReportData;

public interface ReportRepository {
  ReportData getRevenueReport(CreateReportRequest request);
}
