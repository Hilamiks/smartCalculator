package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static boolean running = true;
    static Pattern additionPattern = Pattern.compile("[+]+");
    static Pattern subtractionPattern = Pattern.compile("[-]+");
    static Pattern commandPattern = Pattern.compile("^/");
    static Pattern invalidSymbolCheck = Pattern.compile("[^-+=\\s\\w]|\\+$|-$");

    static Pattern varAssignPattern = Pattern.compile("=");

    static Pattern varOperationsPattern = Pattern.compile("[a-zA-Z]");

    static Pattern invalidVarNamePattern = Pattern.compile("[^a-zA-Z]");

    static Map<String, Integer> customVars = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (running) {
            String command = scanner.nextLine();
            Matcher commandDetector = commandPattern.matcher(command);
            if (commandDetector.find()) {
                if (command.equals("/exit")) {
                    running = false;
                    System.out.println("Bye!");
                    break;
                } else if (command.equals("/help")) {
                    System.out.println("The program calculates the sum or difference of numbers, you can assign custom variables using \"=\" operator.");
                } else {
                    System.out.println("Unknown command");
                }
            } else if (command.isEmpty()) {

            } else {
                operate(command);
            }
        }
    }

    public static void operate(String command) {
        command = command.replaceAll("\\s+","");
        command = command.replaceAll("--","+");
        command = command.replaceAll("[+]-","-");
        Matcher invalidSymbolMatcher = invalidSymbolCheck.matcher(command);
        command = command.replaceAll("[+]+"," + ");
        command = command.replaceAll("-"," - ");
        command = command.replaceAll("="," = ");
        command = command.replaceFirst("^ - ","0 - ");
        command = command.replaceFirst("^ \\+ ", "0 + ");
        Matcher varAssignMatcher = varAssignPattern.matcher(command);
        Matcher varOperationsMatcher = varOperationsPattern.matcher(command);
        boolean varOps = varOperationsMatcher.find();
        boolean assigner = varAssignMatcher.find();
        boolean error = invalidSymbolMatcher.find();
        if(error) {
            System.out.println("Invalid expression");
        }
        else {
            if (!assigner && !varOps) {
                addSubtract(command);
            } else if (assigner) {
                assignVar(command);
            }
            else if (!assigner && varOps){
                doVarOp(command);
            }
        }
    }

    public static void assignVar(String command) {
        String[] operations = command.split(" ");
        boolean wrongVarName = nameIsWrong(command);
        boolean invalidAssignment = assignmentIsWrong(command);
        for (String s : operations) System.out.println(s);
        if (wrongVarName) {
            System.out.println("Invalid identifier");
        } else if (invalidAssignment) {
            System.out.println("Invalid Assignment");
        } else {
            if (customVars.containsKey(operations[2])) {
                operations[2] = ""+customVars.get(operations[2]);
            }
            customVars.put(operations[0],Integer.valueOf(operations[2]));
        }
    }

    public static boolean assignmentIsWrong(String command) {
        String[] operations = command.split(" ");
        int equalsCounter = 0;
        for (int i = 0; i < operations.length; i++) {
            if (operations[i].contains("=")) {
                equalsCounter++;
            }
        }
        return equalsCounter>1;
    }

    public static boolean nameIsWrong (String command) {
        String[] operations = command.split(" ");
        Matcher invalidVarNameMatcher = invalidVarNamePattern.matcher(operations[0]);
        return invalidVarNameMatcher.find();
    }
    public static void doVarOp (String command) {
        String[] operations = command.split(" ");
        boolean wrongVarName = nameIsWrong(command);
        boolean unknownVar = false;
        for (int i = 0; i < operations.length; i++) {
            if (!customVars.containsKey(operations[i]) &&
                    !(operations[i].contains("+") || operations[i].contains("-") ||
                            operations[i].contains("=") || operations[i].matches("\\d"))) {
                unknownVar = true;
            }
        }
        if (wrongVarName) {
            System.out.println("Invalid identifier");
        } else if (unknownVar) {
            System.out.println("Unknown identifier");
        } else if (operations.length == 1) {
            if (customVars.containsKey(operations[0])) {
                System.out.println(customVars.get(operations[0]));
            } else {
                System.out.println("Unknown variable");
            }
        } else {
            for (int i = 0; i < operations.length; i++) {
                if (!operations[i].contains("+") && !operations[i].contains("-") && !operations[i].matches("\\d")) {
                    operations[i] = ""+customVars.get(operations[i]);
                }
            }
            for (String s : operations) System.out.println(s);
            addSubtract(operations);
        }
    }

    public static void addSubtract(String command) {
        String[] operations = command.split(" ");
        int c = Integer.parseInt(operations[0]);
        for (int i = 0; i < operations.length; i++) {
            Matcher additionMatcher = additionPattern.matcher(operations[i]);
            Matcher subtractionMatcher = subtractionPattern.matcher(operations[i]);
            if (additionMatcher.matches()) {
                int a = Integer.parseInt(operations[i+1]);
                c += a;
            } else if (subtractionMatcher.matches()) {
                int a = Integer.parseInt(operations[i+1]);
                c -= a;
            }
        }
        System.out.println(c);
    }

    public static void addSubtract(String[] operations) {
        int c = Integer.parseInt(operations[0]);
        for (int i = 0; i < operations.length; i++) {
            Matcher additionMatcher = additionPattern.matcher(operations[i]);
            Matcher subtractionMatcher = subtractionPattern.matcher(operations[i]);
            if (additionMatcher.matches()) {
                int a = Integer.parseInt(operations[i+1]);
                c += a;
            } else if (subtractionMatcher.matches()) {
                int a = Integer.parseInt(operations[i+1]);
                c -= a;
            }
        }
        System.out.println(c);
    }
}
