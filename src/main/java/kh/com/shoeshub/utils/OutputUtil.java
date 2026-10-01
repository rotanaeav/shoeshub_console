package kh.com.shoeshub.utils;

import static kh.com.shoeshub.utils.ColorUtil.*;

public class OutputUtil {

    public static void print(String message) {
        System.out.print(message);
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + "✔ " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + "✖ " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "⚠ " + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(CYAN + "ℹ " + message + RESET);
    }

    public static void printHeader(String title) {
        String border = "═".repeat(title.length() + 8);
        System.out.println(CYAN + "\n╔" + border + "╗");
        System.out.println("║    " + BOLD + title.toUpperCase() + RESET + CYAN + "    ║");
        System.out.println("╚" + border + "╝" + RESET);
    }

    public static void printSubHeader(String title) {
        System.out.println(YELLOW + "\n─── " + BOLD + title + RESET + YELLOW + " ───" + RESET);
    }
}
