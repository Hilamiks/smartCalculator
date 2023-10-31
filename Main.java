package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static boolean running = true;

    private static final Map<String, Integer> customVars = new HashMap<>();

    static Pattern invalidCharPattern = Pattern.compile("[^-+=\\w\\s]");

    static Pattern digitPattern = Pattern.compile("\\d+");

    static Pattern varNamePattern = Pattern.compile("[a-zA-Z]+");

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (running) {
            String command = scanner.nextLine();
            command = formatCommand(command);
            executeCommand(command);
        }
    }

    private static String formatCommand(String command) {
        command = command.replaceAll("\\s","");
        if (command.startsWith("-")) {
            command = "0+"+command;
        }
        command = command.replaceAll("--","+");
        command = command.replaceAll("[+]+","+");
        command = command.replaceAll("\\+-","-");
        command = command.replaceAll("=+"," = ");
        command = command.replaceAll("\\+", " + ");
        command = command.replaceAll("-"," - ");
        command = command.replaceAll("\\s+"," ");
        return command;
    }

    private static void performOperation(String[] splitCom) {
        for (int i = 0; i < splitCom.length; i++) {
            if(varIsName(splitCom[i]) && customVars.containsKey(splitCom[i])) {
                splitCom[i] = ""+customVars.get(splitCom[i]);
            }
        }
        int c = Integer.parseInt(splitCom[0]);
        for (int i = 1; i < splitCom.length; i++) {
            if(splitCom[i].equals("+")) {
                c += Integer.parseInt(splitCom[i+1]);
            } else if (splitCom[i].equals("-")) {
                c -= Integer.parseInt(splitCom[i+1]);
            }
        }
        System.out.println(c);
    }

    private static void assignVar(String[] splitCom) {
        if (varIsName(splitCom[0])) {
            if (splitCom[2].equals("-")){
                assignVarHelper(3, splitCom, true);
            } else {
                assignVarHelper(2,splitCom, false);
            }
        } else {
            System.out.println("Invalid identifier");
        }
    }

    private static boolean varIsDigit(String s) {
        return digitPattern.matcher(s).matches();
    }

    private static boolean varIsName(String s) {
        return varNamePattern.matcher(s).matches();
    }

    private static void assignVarHelper (int i, String[] splitCom, boolean negative) {
        boolean varIsDigit = varIsDigit(splitCom[i]);
        boolean varIsName = varIsName(splitCom[i]);
        if (varIsDigit) {
            if (negative) {
                customVars.put(splitCom[0], -Integer.parseInt(splitCom[i]));
            } else {
                customVars.put(splitCom[0], Integer.parseInt(splitCom[i]));
            }
        } else if (varIsName && customVars.containsKey(splitCom[i])) {
            customVars.put(splitCom[0], customVars.get(splitCom[i]));
        } else if (varIsName && !customVars.containsKey(splitCom[i])){
            System.out.println("Unknown Variable");
        }
    }

    private static void menu(String command) {
        if (command.equals("/exit")) {
            System.out.println("Bye!");
            running = false;
        } else if (command.equals("/help")) {
            System.out.println("This calculator allows you to add or subtract whole numbers or custom variables (assigned with \"=\" sign)");
        } else {
            System.out.println("Unknown command");
        }
    }

    private static void executeCommand(String command) {
        String[] splitCom = command.split(" ");
        boolean invalidChar = invalidCharPattern.matcher(command).find();
        if (command.startsWith("/")) {
            menu(command);
        } else if (invalidChar) {
            System.out.println("Invalid Expression");
        } else if (command.contains("=")) {
            assignVar(splitCom);
        } else if (customVars.containsKey(command)) {
            System.out.println(customVars.get(command));
        } else if (command.contains("+") || command.contains("-")){
            performOperation(splitCom);
        } else {}
    }
}
