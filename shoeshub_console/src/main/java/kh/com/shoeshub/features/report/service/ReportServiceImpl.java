package kh.com.shoeshub.features.report.service;

import kh.com.shoeshub.features.report.repository.ReportRepository;
import kh.com.shoeshub.features.report.repository.ReportRepositoryImpl;

public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository = new ReportRepositoryImpl();

    // TODO: Implement reporting business logic & file export
}
