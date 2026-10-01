package kh.com.shoeshub.utils;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.Table;

public class TableUtil {

    public static Table createTable(int columns, String... headers) {
        Table table = new Table(columns, BorderStyle.UNICODE_BOX_DOUBLE_BORDER);
        CellStyle headerStyle = new CellStyle(CellStyle.HorizontalAlign.CENTER);
        for (String header : headers) {
            table.addCell(ColorUtil.BOLD + header + ColorUtil.RESET, headerStyle);
        }
        return table;
    }

    public static void render(Table table) {
        System.out.println(table.render());
    }
}
