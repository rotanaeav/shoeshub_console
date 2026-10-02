package kh.com.shoeshub.features.report;

import kh.com.shoeshub.features.report.service.ReportService;
import kh.com.shoeshub.features.report.service.ReportServiceImpl;

public class ReportController {

    private final ReportService reportService = new ReportServiceImpl();
    private final ReportUI reportUI = new ReportUI();

    public void handleViewRevenue() {
        // TODO: Implement view revenue
    }
}
