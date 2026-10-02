package kh.com.shoeshub.utils;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.Table;

import static kh.com.shoeshub.utils.ColorUtil.*;

public class OutputUtil {

    public static void print(String message) {
        IO.print(message);
    }

    public static void println(String message) {
        IO.println(message);
    }

    public static void printSuccess(String message) {
        IO.println(GREEN + "✔ " + message + RESET);
    }

    public static void printError(String message) {
        IO.println(RED + "✖ " + message + RESET);
    }

    public static void printWarning(String message) {
        IO.println(YELLOW + "⚠ " + message + RESET);
    }

    public static void printInfo(String message) {
        IO.println(CYAN + "ℹ " + message + RESET);
    }

    public static void printBanner() {
        String banner = CYAN + """
           _____ __                     __  __      __
          / ___// /_  ____  ___  _____ / / / /_  __/ /_
          \\__ \\/ __ \\/ __ \\/ _ \\/ ___// /_/ / / / / __ \\
         ___/ / / / / /_/ /  __(__  )/ __  / /_/ / /_/ /
        /____/_/ /_/\\____/\\___/____//_/ /_/\\__,_/_.___/
        """ + RESET;
        IO.println(banner);
    }

    public static void printHeader(String title) {
        Table table = new Table(1, BorderStyle.UNICODE_ROUND_BOX_WIDE);
        CellStyle style = new CellStyle(CellStyle.HorizontalAlign.CENTER);
        table.addCell("   " + BOLD + title.toUpperCase() + RESET + "   ", style);
        IO.println();
        TableUtil.render(table);
    }

    public static void printSubHeader(String title) {
        IO.println(YELLOW + "\n─── " + BOLD + title + RESET + YELLOW + " ───" + RESET);
    }
}
