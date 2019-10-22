package com.sekhanov.scriptparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * ScriptParser
 */
public class ScriptExecutor {

    private List<String> statementList;
    private Scanner fileScanner;
    private Scanner inputScanner;
    private Map<String, Number> variables;

    private int currentStatement;

    public ScriptExecutor(Scanner fileScanner) {
        this.fileScanner = fileScanner;
        inputScanner = new Scanner(System.in);
        statementList = new ArrayList<>();
        variables = new HashMap<>();
    }

    public void testFile() {
        while (fileScanner.hasNextLine()) {
            System.out.println(fileScanner.nextLine());
        }

    }

    public void addStatements() {
        String nextCommand = "";
        while (fileScanner.hasNext()) {
            fileScanner.useDelimiter("[\\n:]");
            nextCommand = fileScanner.next().trim().toLowerCase();
            if (nextCommand.equals("")) {
                continue;
            }
            statementList.add(nextCommand);
        }
        statementList.forEach(System.out::println);
    }

    public void startExecutingScript() {
        while(currentStatement < statementList.size()) {
            parseStatement(statementList.get(currentStatement));
        }
        System.out.println("end of script");
    }

    private void parseStatement(String statement) {
        if (statement.startsWith("if")) {
            executeConditionalStatement(statement);
        }
    }

    private void executeConditionalStatement(String command) {
        String[] parts = command.split(" ");
        boolean conditionTrue = checkCondition(parts);
        if(conditionTrue) {
            currentStatement = statementList.indexOf(parts[5]);
        } else {
            currentStatement++;
        }
    }

    private boolean checkCondition(String[] parts) {
        int firstOperand = getOperandValue(parts[1]);
        int secondOperand = getOperandValue(parts[3]);
        switch (parts[2]) {
        case "==":
            return firstOperand == secondOperand;
        case "!=":
            return firstOperand != secondOperand;
        default:
            break;
        }
        //TODO other operation
        return false;

    }

    private int getOperandValue(String string) {
       //TODO начинается с буквы - берем из коллекции, если целиком число, то его возвращаем)
        return 0;
    }

}
