package com.sekhanov.scriptparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Основной класс для парсинка скрипта
 */
public class ScriptExecutor {

    private static final String TAG_STATEMENT_REG_EX = "^\\s*\\w+:\\s*$";
    private static final String PRINT_STATEMENT_REG_EX = "^\\s*print\\s+\\w+\\s*$";
    private static final String READ_STATEMENT_REG_EX = "^\\s*read\\s+\\w+\\s*$";
    private static final String GOTO_STATEMENT_REG_EX = "^\\s*goto\\s+\\w+\\b\\s*$";
    private static final String VARIABLE_ASSIGNMENT_STATEMENT_REG_EX = "^\\s*\\w+\\s+=\\s+\\w+\\s*$";
    private static final String VARIABLE_STATEMENT_REG_EX = "^\\s*\\w+\\s+=\\s+\\w+\\s+[*+-\\\\%]\\s+\\w+\\s*$";
    private static final String CONDITION_STATEMENT_REG_EX = "^\\s*if\\s+\\w+\\s+(>|<|==|!=|>=|<=)\\s+\\w+\\s+goto\\s+\\w+\\s*$";
    private static final String SPACES_REG_EX = "\\s+";
    private static final String NUMBERS_REG_EX = "\\d+";
    private static final String NEW_LINE_REG_EX = "(\\n|\\n\\r)";

    /**
     * Полный список всех команд, считанных из файла скрипта
     */
    private List<String> statementList;
    private Scanner fileScanner;
    private Scanner inputScanner;
    /**
     * Переменные полученные из скрипта
     */
    private Map<String, Integer> variables;
    /**
     * Маркер, указывающий на текущую команду
     */
    private int currentStatement;

    public ScriptExecutor(Scanner fileScanner) {
        this.fileScanner = fileScanner;
        inputScanner = new Scanner(System.in);
        statementList = new ArrayList<>();
        variables = new HashMap<>();
    }

    /**
     * Метод разбивает скрипт на отдельные команды по символам новой строки. Если команда и метка
     * находятся на одной строке, то они разбиваются на две отдельные команды. Так же убираются
     * все лишние пробелы по краям и вся строка преобразуется в нижний регистр
     * (скрипт не чувствителен к регистру)
     */
    public void addStatements() {
        String nextCommand = "";
        while (fileScanner.hasNext()) {
            fileScanner.useDelimiter(NEW_LINE_REG_EX);
            nextCommand = fileScanner.next().trim().toLowerCase();
            if (nextCommand.equals("")) {
                continue;
            }
            if(nextCommand.contains(":") && nextCommand.split(":").length > 1) {
                String[] parts = nextCommand.split(":");
                statementList.add((parts[0] + ":").trim());
                statementList.add(parts[1].trim());
                continue;
            }
            statementList.add(nextCommand);
        }
        statementList.forEach(System.out::println);
    }

    /**
     * цикл исполнения всех команд скрипта
     */
    public void executeScript() {
        while (currentStatement < statementList.size()) {
            parseStatementType(statementList.get(currentStatement));
        }
        // System.out.println("end of script");
    }

    /**
     * С помощью регулярных выражений метод определяет тип команды и вызывает
     * соответствующий метод
     *
     * @param statement отдельная команда
     */
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
        throw new IllegalScriptValueException("syntax at statement number " + (currentStatement + 1) + " is not correct");
    }

    /**
     * Метод разбивает команду на отдельные аргументы, в зависимости от оператора
     * выполняет выражение из двух операндов и присваивает значение результата
     * выражения переменной
     *
     * @param statement команда соответствующая регулярному выражению
     *                  {@link ScriptExecutor#VARIABLE_STATEMENT_REG_EX}
     */
    private void assignVariableFromExpression(String statement) {
        String[] parts = statement.split(SPACES_REG_EX);
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

    /**
     * переход к исполнению блока команд по метке из второго аргумента строки
     *
     * @param statement команда соответствующая регулярному выражению
     *                  {@link ScriptExecutor#TAG_STATEMENT_REG_EX}
     */
    private void goToTag(String statement) {
        String[] parts = statement.split(SPACES_REG_EX);
        if (statementList.contains(parts[1] + ":")) {
            currentStatement = statementList.indexOf(parts[1] + ":");
        } else {
            throw new IllegalScriptValueException("tag value '" + parts[5] + "' at the line number " + (currentStatement + 1) + " is not correct");
        }

    }

    /**
     * инициализация переменной или перезапись значения переменной
     *
     * @param statement команда соответствующая регулярному выражению
     *                  {@link ScriptExecutor#VARIABLE_ASSIGNMENT_STATEMENT_REG_EX}
     */
    private void assignVariableValue(String statement) {
        String[] parts = statement.split(SPACES_REG_EX);
        variables.put(parts[0], getOperandValue(parts[2]));
        currentStatement++;
    }

    /**
     * Считывает число введенное из командной строки и инициализирует переменную
     * или, в случае ее присутствия, присваивает ей новое значение.
     *
     * @param statement команда соответствующая регулярному выражению
     *                  {@link ScriptExecutor#READ_STATEMENT_REG_EX}
     */
    private void readVariableValue(String statement) {
        String[] parts = statement.split(SPACES_REG_EX);
        System.out.print(parts[1] + "=");
        while (!inputScanner.hasNextInt()) {
            inputScanner.next();
            System.out.println("enter the number:");
            System.out.print(parts[1] + "=");
        }
        int value = inputScanner.nextInt();
        variables.put(parts[1], value);
        currentStatement++;
    }

    /**
     * Выводит на экран число или переменную
     *
     * @param statement команда соответствующая регулярному выражению
     *                  {@link ScriptExecutor#PRINT_STATEMENT_REG_EX}
     */
    private void printVarNum(String statement) {
        String[] parts = statement.split(SPACES_REG_EX);
        System.out.println(getOperandValue(parts[1]));
        currentStatement++;
    }

    /**
     * проверяет условие, переданное в команде. В случае истинности переходит к
     * исполнению блока команд по метке из последнего аргумента строки
     *
     * @param statement команда соответствующая регулярному выражению
     *                  {@link ScriptExecutor#CONDITION_STATEMENT_REG_EX}
     */
    private void executeConditionalStatement(String statement) {
        String[] parts = statement.split(SPACES_REG_EX);
        boolean conditionTrue = checkCondition(parts);
        if (conditionTrue) {
            if (statementList.contains(parts[5] + ":")) {
                currentStatement = statementList.indexOf(parts[5] + ":");
            } else {
                throw new IllegalScriptValueException("tag value '" + parts[5] + "' at the line number " + (currentStatement + 1) + " is not correct");
            }
        } else {
            currentStatement++;
        }
    }

    /**
     * Проверка условия
     *
     * @param parts аргументы строки условной команды
     */
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

    /**
     * Получение значения переменно или литерала представленного в строке команды
     *
     * @param varNum отдельный операнд команды
     */
    private int getOperandValue(String varNum) {
        if (varNum.matches(NUMBERS_REG_EX)) {
            return Integer.valueOf(varNum);
        } else {
            return variables.get(varNum);
        }
    }

}
