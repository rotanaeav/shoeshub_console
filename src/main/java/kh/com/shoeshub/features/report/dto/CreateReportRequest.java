package kh.com.shoeshub.features.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public class CreateReportRequest {
    public static final ZoneId ZONE = ZoneId.of("Asia/Phnom_Penh");
    private LocalDate startDate;
    private LocalDate endDate;
    private int topLimit = 10;

    public CreateReportRequest() {}

    public CreateReportRequest(LocalDate startDate, LocalDate endDate, int topLimit) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.topLimit = topLimit;
    }

    public LocalDate getStartDate() {
        return startDate; }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate; }
    public LocalDate getEndDate() {
        return endDate; }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate; }
    public int getTopLimit() {
        return topLimit; }
    public void setTopLimit(int topLimit) {
        this.topLimit = topLimit; }
    public OffsetDateTime fromInclusive() {
        return startDate.atStartOfDay(ZONE).toOffsetDateTime(); }
    public OffsetDateTime toExclusive() {
        return endDate.plusDays(1).atStartOfDay(ZONE).toOffsetDateTime(); }

    public record SalesRow(LocalDate day, long orders, BigDecimal revenue) {}
    public record TopProductRow(UUID productId, String name, long unitsSold, BigDecimal revenue) {}
    public record RevenueSummary(long paidOrders, BigDecimal totalRevenue) {}
    public record ReportData(RevenueSummary summary, List<SalesRow> sales, List<TopProductRow> topProducts) {
        public ReportData {
            sales = List.copyOf(sales);
            topProducts = List.copyOf(topProducts);
        }
    }
}
