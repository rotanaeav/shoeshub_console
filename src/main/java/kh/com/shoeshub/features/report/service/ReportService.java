package kh.com.shoeshub.features.report.service;

import kh.com.shoeshub.features.report.dto.CreateReportRequest;

import java.nio.file.Path;
import java.util.UUID;

public interface ReportService {
    CreateReportRequest.ReportData getReport(UUID currentUserId, CreateReportRequest request);
    Path exportSalesReportToCsv(UUID currentUserId, CreateReportRequest request, Path destination);
}
