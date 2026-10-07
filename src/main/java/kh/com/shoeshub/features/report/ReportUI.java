package kh.com.shoeshub.features.report;

import kh.com.shoeshub.features.report.dto.CreateReportRequest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReportUI {
    public int menu() {
        OutputUtil.printHeader("SALES REPORTS & REVENUE");
        OutputUtil.println("[1] Sales by day   [2] Top products   [3] Revenue   [4] Export CSV   [0] Back");
        return InputUtil.readInt("Choose",0,4);
    }
    private LocalDate date(String prompt) {
        while (true) {
            try { return LocalDate.parse(InputUtil.readRequiredText(prompt+" (YYYY-MM-DD)")); }
            catch (DateTimeParseException e) { OutputUtil.printError("Enter a valid date, for example 2026-10-07."); }
        }
    }
    public CreateReportRequest readRequest() {
        return new CreateReportRequest(date("Start date"),date("End date"),InputUtil.readInt("Top products limit",1,100));
    }
    public void sales(CreateReportRequest.ReportData data) {
        OutputUtil.println("DAY | PAID ORDERS | REVENUE");
        if (data.sales().isEmpty()) OutputUtil.printInfo("No paid sales in this period.");
        for (CreateReportRequest.SalesRow s:data.sales()) OutputUtil.println(s.day()+" | "+s.orders()+" | "+s.revenue().toPlainString());
        revenue(data);
    }
    public void top(CreateReportRequest.ReportData data) {
        OutputUtil.println("PRODUCT ID | PRODUCT | UNITS SOLD | LINE REVENUE");
        if (data.topProducts().isEmpty()) OutputUtil.printInfo("No sold products in this period.");
        for (CreateReportRequest.TopProductRow p:data.topProducts())
            OutputUtil.println(p.productId()+" | "+p.name()+" | "+p.unitsSold()+" | "+p.revenue().toPlainString());
    }
    public void revenue(CreateReportRequest.ReportData data) {
        OutputUtil.println("Paid orders: "+data.summary().paidOrders());
        OutputUtil.println("Total revenue: "+data.summary().totalRevenue().toPlainString());
        OutputUtil.printInfo("Successful payment amounts; dates use Cambodia time. Currency follows your database.");
    }
}
