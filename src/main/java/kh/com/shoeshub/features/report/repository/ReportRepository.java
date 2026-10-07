package kh.com.shoeshub.features.report.repository;

import kh.com.shoeshub.features.report.dto.CreateReportRequest;

import java.util.UUID;

public interface ReportRepository {
    boolean canViewReports(UUID userId);
    CreateReportRequest.ReportData getReport(CreateReportRequest request);
}
