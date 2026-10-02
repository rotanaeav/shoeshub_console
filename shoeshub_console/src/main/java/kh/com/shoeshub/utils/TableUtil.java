package kh.com.shoeshub.utils;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.Table;

import static kh.com.shoeshub.utils.ColorUtil.*;

public class TableUtil {

    public static Table createTable(int columns, String... headers) {
        Table table = new Table(columns, BorderStyle.UNICODE_ROUND_BOX_WIDE);
        CellStyle headerStyle = new CellStyle(CellStyle.HorizontalAlign.CENTER);
        for (String header : headers) {
            table.addCell(BOLD + header + RESET, headerStyle);
        }
        return table;
    }

    public static void render(Table table) {
        render(table, CYAN);
    }

    public static void render(Table table, String borderColor) {
        String rendered = borderColor + table.render().replace(RESET, RESET + borderColor) + RESET;
        IO.println(rendered);
    }
}
