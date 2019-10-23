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

    private static final String TAG_STATEMENT_REG_EX = "^\\s*\\w+\\s*$";
    private static final String PRINT_STATEMENT_REG_EX = "^\\s*print\\s+\\w+\\s*$";
    private static final String READ_STATEMENT_REG_EX = "^\\s*read\\s+\\w+\\s*$";
    private static final String GOTO_STATEMENT_REG_EX = "^\\s*goto\\s+\\w+\\b\\s*$";
    private static final String VARIABLE_ASSIGNMENT_STATEMENT_REG_EX = "^\\s*\\w+\\s+=\\s+\\w+\\s*$";
    private static final String VARIABLE_STATEMENT_REG_EX = "^\\s*\\w+\\s+=\\s+\\w+\\s+[*+-\\\\%]\\s+\\w+\\s*$";
    private static final String CONDITION_STATEMENT_REG_EX = "^\\s*if\\s+\\w+\\s+(>|<|==|!=|>=|<=)\\s+\\w+\\s+goto\\s+\\w+\\s*$";

    private List<String> statementList;
    private Scanner fileScanner;
    private Scanner inputScanner;
    private Map<String, Integer> variables;

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
            fileScanner.useDelimiter("(\\n|\\n\\r|:)");
            nextCommand = fileScanner.next().trim().toLowerCase();
            if (nextCommand.equals("")) {
                continue;
            }
            statementList.add(nextCommand);
        }
        // statementList.forEach(System.out::println);
    }

    public void startExecutingScript() {
        while(currentStatement < statementList.size()) {
            parseStatementType(statementList.get(currentStatement));
        }
        System.out.println("end of script");
    }

    private void parseStatementType(String statement) {
        if (statement.matches(CONDITION_STATEMENT_REG_EX)) {
            executeConditionalStatement(statement);
            return;
        }
        if (statement.matches(PRINT_STATEMENT_REG_EX)) {
            printVarNum(statement);
            return;
        }
        if (statement.matches(GOTO_STATEMENT_REG_EX)) {
            goToTag(statement);
            return;
        }
        if (statement.matches(READ_STATEMENT_REG_EX)) {
            readVariableValue(statement);
            return;
        }
        if (statement.matches(VARIABLE_ASSIGNMENT_STATEMENT_REG_EX)) {
            assignVariableValue(statement);
            return;
        }
        if (statement.matches(VARIABLE_STATEMENT_REG_EX)) {
            assignVariableFromExpression(statement);
            return;
        }
        if (statement.matches(TAG_STATEMENT_REG_EX)) {
            currentStatement++;
            return;
        }
        throw new IllegalScriptValueException("syntax in statement number " + currentStatement + " is not correct");
    }

    private void assignVariableFromExpression(String statement) {
        String[] parts = statement.split("\\s+");
        int firstOperand = getOperandValue(parts[2]);
        int secondOperand = getOperandValue(parts[4]);
        int result = 0;
        switch (parts[3]) {
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "*":
                result = firstOperand * secondOperand;
                break;
            case "/":
                result = firstOperand / secondOperand;
                break;
            case "%":
                result = firstOperand % secondOperand;
                break;
            default:
                break;
        }
        variables.put(parts[0], result);
        currentStatement++;
    }

    private void goToTag(String statement) {
        String[] parts = statement.split("\\s+");
        currentStatement = statementList.indexOf(parts[1]);
    }

    private void assignVariableValue(String statement) {
        String[] parts = statement.split("\\s+");
        variables.put(parts[0], getOperandValue(parts[2]));
        currentStatement++;
    }

    private void readVariableValue(String statement) {
        String[] parts = statement.split("\\s+");
        System.out.print(parts[1] + "=");
        int value = inputScanner.nextInt();
        variables.put(parts[1], value);
        currentStatement++;
    }

    private void printVarNum(String statement) {
        String[] parts = statement.split("\\s+");
        System.out.println(getOperandValue(parts[1]));
        currentStatement++;
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
        case "<":
            return firstOperand < secondOperand;
        case ">":
            return firstOperand > secondOperand;
        case ">=":
            return firstOperand >= secondOperand;
        case "<=":
            return firstOperand <= secondOperand;
        default:
            break;
        }
        return false;
    }

    private int getOperandValue(String varNum) {
       if(varNum.matches("\\d+")) {
           return Integer.valueOf(varNum);
       } else {
           return variables.get(varNum);
       }
    }

}
