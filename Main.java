package calculator;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static boolean running = true;

    static Pattern invalidCharPattern = Pattern.compile("[^-+=\\w\\s]");

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (running) {
            String command = scanner.nextLine();
            command = formatCommand(command);
            boolean invalidChar = invalidCharPattern.matcher(command).find();
            String[] splitCom = command.split(" ");
            if (command.startsWith("/")) {
                menu(command);
            } else if (invalidChar) {
                System.out.println("");
            }
        }
    }

    public static String formatCommand(String command) {
        return command;
    }

    public static void menu(String command) {
        if (command.equals("/exit")) {
            System.out.println("Bye!");
            running = false;
        } else if (command.equals("/help")) {
            System.out.println("This calculator allows you to add or subtract whole numbers or custom variables (assigned with \"=\" sign)");
        } else {
            System.out.println("Unknown command");
        }
    }
}
