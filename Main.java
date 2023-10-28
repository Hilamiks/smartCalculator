package calculator;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static boolean running = true;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Pattern additionPattern = Pattern.compile("[+]+");
        Pattern subtractionPattern = Pattern.compile("[-]+");
        Pattern commandPattern = Pattern.compile("^/");
        Pattern invalidSymbolCheck = Pattern.compile("[^-\\d+\\s]|\\+$|-$");
        while (running) {
            String command = scanner.nextLine();
            Matcher commandDetector = commandPattern.matcher(command);
            if (commandDetector.find()) {
                if (command.equals("/exit")) {
                    running = false;
                    System.out.println("Bye!");
                    break;
                } else if (command.equals("/help")) {
                    System.out.println("The program calculates the sum or difference of numbers");
                } else {
                    System.out.println("Unknown command");
                }
            } else if (command.isEmpty()) {

            } else {
                command = command.replaceAll("\\s+","");
                command = command.replaceAll("--","+");
                command = command.replaceAll("[+]-","-");
                Matcher invalidSymbolMatcher = invalidSymbolCheck.matcher(command);
                command = command.replaceAll("[+]+"," + ");
                command = command.replaceAll("-"," - ");
                command = command.replaceFirst("^ - ","0 - ");
                command = command.replaceFirst("^ \\+ ", "0 + ");
                boolean error = invalidSymbolMatcher.find();
                if(error) {
                    System.out.println("Invalid expression");
                }
                else {
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
            }
        }
    }
}
