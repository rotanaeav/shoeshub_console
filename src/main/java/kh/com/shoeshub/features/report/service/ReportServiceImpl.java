package kh.com.shoeshub.features.report.service;

import kh.com.shoeshub.features.report.dto.CreateReportRequest;
import kh.com.shoeshub.features.report.repository.ReportRepository;
import kh.com.shoeshub.features.report.repository.ReportRepositoryImpl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository = new ReportRepositoryImpl();

    public ReportServiceImpl(ReportRepository repository) { reportRepository= Objects.requireNonNull(repository); }
    private void validate(UUID user, CreateReportRequest r) {
        if (user==null || !reportRepository.canViewReports(user))
            throw new ValidationException("Only active admins and sellers may view reports.");
        if (r==null || r.getStartDate()==null || r.getEndDate()==null)
            throw new ValidationException("Start and end dates are required.");
        if (r.getStartDate().isAfter(r.getEndDate())) throw new ValidationException("Start date must not be after end date.");
        if (r.getStartDate().getYear()<1 || r.getEndDate().getYear()>9998)
            throw new ValidationException("Use dates with years from 0001 through 9998.");
        if (r.getTopLimit()<1 || r.getTopLimit()>100) throw new ValidationException("Top product limit must be 1 to 100.");
    }
    @Override public CreateReportRequest.ReportData getReport(UUID user, CreateReportRequest r) {
        validate(user,r); return reportRepository.getReport(r);
    }
    // Quote every CSV field; neutralize spreadsheet formulas in text from product names.
    private String csv(Object value) {
        String s=Objects.toString(value,"");
        if (value instanceof String) {
            String trimmed=s.stripLeading();
            if (!trimmed.isEmpty() && "=+-@".indexOf(trimmed.charAt(0))>=0) s="'"+s;
        }
        return "\""+s.replace("\"","\"\"")+"\"";
    }
    private void row(BufferedWriter writer, Object... cells) throws IOException {
        List<String> values=new ArrayList<>();
        for (Object cell:cells) values.add(csv(cell));
        writer.write(String.join(",",values)); writer.write("\r\n");
    }
    @Override public Path exportSalesReportToCsv(UUID user, CreateReportRequest r, Path destination) {
        CreateReportRequest.ReportData data=getReport(user,r);
        if (destination==null) throw new ValidationException("Export destination is required.");
        Path out=destination.toAbsolutePath().normalize();
        Path temp=null;
        try {
            Files.createDirectories(out.getParent());
            temp=Files.createTempFile(out.getParent(),"shoehub-report-",".tmp");
            try (BufferedWriter writer=Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                row(writer,"ShoeHub Sales Report",r.getStartDate(),r.getEndDate(),"Asia/Phnom_Penh");
                row(writer,"Paid orders","Revenue"); row(writer,data.summary().paidOrders(),data.summary().totalRevenue());
                row(writer,"Day","Orders","Revenue");
                for (CreateReportRequest.SalesRow sale:data.sales()) row(writer,sale.day(),sale.orders(),sale.revenue());
                row(writer,"Product ID","Product","Units sold","Line revenue");
                for (CreateReportRequest.TopProductRow product:data.topProducts())
                    row(writer,product.productId(),product.name(),product.unitsSold(),product.revenue());
            }

            Files.move(temp,out); return out;
        } catch (IOException e) {
            throw new AppException("Cannot export CSV: "+e.getMessage(),e);
        }
        finally {
            if (temp!=null) try {
                Files.deleteIfExists(temp);
            } catch (IOException ignored) {} }
    }
}
