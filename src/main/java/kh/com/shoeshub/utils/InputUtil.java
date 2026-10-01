package kh.com.shoeshub.utils;

import java.util.Scanner;
import java.util.UUID;

import static kh.com.shoeshub.utils.ColorUtil.*;
import static kh.com.shoeshub.utils.OutputUtil.*;

public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);

    public static String readText(String prompt) {
        print(CYAN + ">> " + prompt + ": " + RESET);
        return scanner.nextLine().trim();
    }

    public static String readRequiredText(String prompt) {
        while (true) {
            String input = readText(prompt);
            if (!input.isEmpty()) {
                return input;
            }
            printError("Input cannot be empty. Please try again.");
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readRequiredText(prompt));
            } catch (NumberFormatException e) {
                printError("Invalid integer. Please enter a valid number.");
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            printError(String.format("Please enter a number between %d and %d.", min, max));
        }
    }

    public static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            printError("Number must be greater than 0.");
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readRequiredText(prompt));
            } catch (NumberFormatException e) {
                printError("Invalid decimal number. Please try again.");
            }
        }
    }

    public static double readDouble(String prompt, double min) {
        while (true) {
            double value = readDouble(prompt);
            if (value >= min) {
                return value;
            }
            printError(String.format("Value must be at least %.2f.", min));
        }
    }

    public static boolean readConfirm(String prompt) {
        while (true) {
            String input = readText(prompt + " (y/n)").toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            printError("Please enter 'y' for Yes or 'n' for No.");
        }
    }

    public static UUID readUUID(String prompt) {
        while (true) {
            String input = readRequiredText(prompt);
            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                printError("Invalid UUID format. Example: 123e4567-e89b-12d3-a456-426614174000");
            }
        }
    }

    public static <E extends Enum<E>> E readEnum(String title, Class<E> enumClass) {
        E[] constants = enumClass.getEnumConstants();
        printSubHeader(title);
        for (int i = 0; i < constants.length; i++) {
            println(String.format(" [%d] %s", i + 1, constants[i].name()));
        }
        int choice = readInt("Select option (1-" + constants.length + ")", 1, constants.length);
        return constants[choice - 1];
    }

    public static void pressEnter() {
        print(YELLOW + "\nPress ENTER to continue..." + RESET);
        scanner.nextLine();
    }
}
