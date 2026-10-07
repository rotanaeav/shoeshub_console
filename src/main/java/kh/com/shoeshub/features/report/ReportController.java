package kh.com.shoeshub.features.report;

import kh.com.shoeshub.features.report.dto.CreateReportRequest;
import kh.com.shoeshub.features.report.service.ReportService;
import kh.com.shoeshub.features.report.service.ReportServiceImpl;

import java.nio.file.Path;
import java.util.UUID;

public class ReportController {

    private final ReportService reportService = new ReportServiceImpl();
    private final ReportUI reportUI = new ReportUI();

    private UUID currentUserId;
    public ReportController() {}
    public ReportController(UUID userId) {
        setCurrentUser(userId);
    }
    public void setCurrentUser(UUID userId) {
        currentUserId=userId;
    }

    public void handleViewRevenue() {
        while (true) {
            int choice=reportUI.menu(); if (choice==0) return;
            try {
                CreateReportRequest request=reportUI.readRequest();
                if (choice==4) {
                    Path destination=Path.of("exports","sales-"+request.getStartDate()+"-"+request.getEndDate()+"-"+ UUID.randomUUID()+".csv");
                    OutputUtil.printSuccess("CSV saved: "+reportService.exportSalesReportToCsv(currentUserId,request,destination));
                } else {
                    CreateReportRequest.ReportData data=reportService.getReport(currentUserId,request);
                    switch (choice) {
                        case 1 -> reportUI.sales(data);
                        case 2 -> reportUI.top(data);
                        case 3 -> reportUI.revenue(data);
                    }
                }
            } catch (AppException e) {
                OutputUtil.printError(e.getMessage());
            }
        }
    }
}
