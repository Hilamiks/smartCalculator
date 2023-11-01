package calculator;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

public class Main {

    private static boolean running = true;

    private static final Map<String, BigInteger> customVars = new HashMap<>();

    private static final Pattern invalidCharPattern = Pattern.compile("[^-+=/*^)(\\w\\s]|[/*]{2,}");

    private static final Pattern possibleOperandsPattern = Pattern.compile("[-+/*^)(]");

    private static final Pattern invalidEndingPattern = Pattern.compile("[^)\\w]");

    private static final Pattern digitPattern = Pattern.compile("\\d+");

    private static final Pattern varNamePattern = Pattern.compile("[a-zA-Z]+");

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (running) {
            String commandRaw = scanner.nextLine();
            executeCommand(commandRaw);
        }
    }

    private static String formatCommand(String command) {
        command = command.replaceAll("\\s","");
        if (command.startsWith("-")) {
            command = "0+"+command;
        }
        command = command.replaceAll("--","+");
        command = command.replaceAll("[+]+","+");
        //command = command.replaceAll("[*]+","*");
        //command = command.replaceAll("/+","/");
        command = command.replaceAll("\\+-","-");
        command = command.replaceAll("=+"," = ");
        command = command.replaceAll("\\+", " + ");
        command = command.replaceAll("-"," - ");
        command = command.replaceAll("[*]"," * ");
        command = command.replaceAll("[/]"," / ");
        command = command.replaceAll("\\^"," ^ ");
        command = command.replaceAll("\\("," ( ");
        command = command.replaceAll("\\)"," ) ");
        command = command.replaceAll("\\s+"," ");
        return command;
    }

    private static void performOperation(String[] splitCom) {
        //checks validity and replaces var names with values in one loop
        int leftParCounter = 0;
        int rightParCounter = 0;
        for (int i = 0; i < splitCom.length; i++) {
            if(varIsName(splitCom[i]) && customVars.containsKey(splitCom[i])) {
                splitCom[i] = ""+customVars.get(splitCom[i]);
            } else if (splitCom[i].equals("(")) {
                leftParCounter++;
            } else if (splitCom[i].equals(")")) {
                rightParCounter++;
            }
        }
        String newCom = "";
        for (int i = 0; i < splitCom.length; i++) {
            newCom = newCom+splitCom[i];
        }
        newCom = formatCommand(newCom);
        splitCom = newCom.split(" ");
        //starts counting if everything's ok
        if (leftParCounter != rightParCounter) {
            throwInvalidExpression();
        } else {
            Deque<String> postfix = createPostfix(splitCom);
            count(postfix);
        }
    }

    private static void count(Deque<String> postfix){
        Deque<String> counter = new ArrayDeque<>();
        while(!postfix.isEmpty()){
            if (varIsDigit(postfix.peekFirst())) {
                counter.push(postfix.pop());
            } else if (postfix.peekFirst().equals("+")) {
                BigInteger a = new BigInteger(counter.pop());
                BigInteger b = new BigInteger(counter.pop());
                BigInteger c = b.add(a);
                counter.push(""+c);
                postfix.removeFirst();
            } else if (postfix.peekFirst().equals("-")) {
                BigInteger a = new BigInteger(counter.pop());
                BigInteger b = new BigInteger(counter.pop());
                BigInteger c = b.subtract(a);
                counter.push(""+c);
                postfix.removeFirst();
            } else if (postfix.peekFirst().equals("*")) {
                BigInteger a = new BigInteger(counter.pop());
                BigInteger b = new BigInteger(counter.pop());
                BigInteger c = b.multiply(a);
                counter.push(""+c);
                postfix.removeFirst();
            } else if (postfix.peekFirst().equals("/")) {
                BigInteger a = new BigInteger(counter.pop());
                BigInteger b = new BigInteger(counter.pop());
                BigInteger c = b.divide(a);
                counter.push(""+c);
                postfix.removeFirst();
            } else if (postfix.peekFirst().equals("^")) {
                BigInteger a = new BigInteger(counter.pop());
                BigInteger b = new BigInteger(counter.pop());
                BigInteger c = BigInteger.ONE;
                for (BigInteger i = BigInteger.ZERO; a.compareTo(i) > 0; i = i.add(BigInteger.ONE)){
                    c = c.multiply(b);
                }
                counter.push(""+c);
                postfix.removeFirst();
            }
        }
        System.out.println(counter.pop());
    }

    private static Deque<String> createPostfix(String[] splitCom) {
        String[] splitComNew = new String[splitCom.length+2];
        splitComNew[0] = "(";
        for (int i = 1; i < splitComNew.length - 1; i++) {
            splitComNew[i] = splitCom[i-1];
        }
        splitComNew[splitComNew.length-1] = ")";
        splitCom = splitComNew;
        Deque<String> postfixNotation = new ArrayDeque<>();
        Deque<String> helper = new ArrayDeque<>();
        for (int i = 0; i < splitCom.length; i++) {
            if (varIsDigit(splitCom[i])) {
                postfixNotation.addLast(splitCom[i]);
            } else if (splitCom[i].equals("(")) {
                helper.addLast(splitCom[i]);
            } else if (splitCom[i].equals(")")) {
                if (!helper.getLast().equals("(")) {
                    postfixNotation.addLast(helper.getLast());
                    helper.removeLast();
                    i--;
                } else {
                    helper.removeLast();
                }
            }
            else {
                if (helper.isEmpty() || helper.peekLast().equals("(")) {
                    helper.addLast(splitCom[i]);
                } else if (getPriority(splitCom[i]) > getPriority(helper.peekLast())) {
                    helper.addLast(splitCom[i]);
                } else if ((getPriority(splitCom[i]) == getPriority(helper.peekLast()))
                || (getPriority(splitCom[i]) < getPriority(helper.peekLast()))) {
                    postfixNotation.addLast(helper.getLast());
                    helper.removeLast();
                    i--;
                }
            }
        }
        return postfixNotation;
    }

    private static int getPriority(String element) {
        return switch (element) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }

    private static void assignVar(String[] splitCom) {
        int equalCounter = 0;
        for (int i = 0; i < splitCom.length; i++) {
            if (splitCom[i].equals("=")) {
                equalCounter++;
            }
        }
        if (equalCounter < 2) {
            if (varIsName(splitCom[0])) {
                if (splitCom[2].equals("-")){
                    assignVarHelper(3, splitCom, true);
                } else {
                    assignVarHelper(2,splitCom, false);
                }
            } else {
                System.out.println("Invalid identifier");
            }
        } else {
            throwInvalidAssignment();
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
                customVars.put(splitCom[0], new BigInteger(splitCom[i]).negate());
            } else {
                customVars.put(splitCom[0], new BigInteger(splitCom[i]));
            }
        } else if (varIsName && customVars.containsKey(splitCom[i])) {
            customVars.put(splitCom[0], customVars.get(splitCom[i]));
        } else if (varIsName && !customVars.containsKey(splitCom[i])){
            throwUnknownVariable();
        } else if (!varIsName) {
            throwInvalidAssignment();
        }
    }

    private static void throwInvalidAssignment() {
        System.out.println("Invalid assignment");
    }

    private static void throwUnknownVariable() {
        System.out.println("Unknown variable");
    }

    private static void throwInvalidExpression() {
        System.out.println("Invalid expression");
    }

    private static void menu(String command) {
        if (command.equals("/exit")) {
            System.out.println("Bye!");
            running = false;
        } else if (command.equals("/help")) {
            System.out.println("This calculator allows you to perform operations on whole numbers or custom variables (assigned with \"=\" sign)");
        } else {
            System.out.println("Unknown command");
        }
    }

    private static void executeCommand(String commandRaw) {
        String command = formatCommand(commandRaw);
        String[] splitCom = command.split(" ");
        boolean invalidChar = invalidCharPattern.matcher(commandRaw).find();
        if (commandRaw.startsWith("/")) {
            menu(commandRaw);
        } else if (invalidChar || invalidEndingPattern.matcher(splitCom[splitCom.length-1]).find()) {
            throwInvalidExpression();
        } else if (command.contains("=")) {
            assignVar(splitCom);
        } else if (customVars.containsKey(command)) {
            System.out.println(customVars.get(command));
        } else if (possibleOperandsPattern.matcher(command).find()){
            performOperation(splitCom);
        } else if (command.isBlank()) {

        }else if (!customVars.containsKey(command)){
            throwUnknownVariable();
        }
    }
}
