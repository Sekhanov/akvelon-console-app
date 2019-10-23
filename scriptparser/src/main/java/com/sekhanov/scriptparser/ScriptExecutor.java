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

    /**
     * Регулярные выражения для определения типа команды
     */
    private static final String TAG_STATEMENT_REG_EX = "^\\s*\\w+\\s*$";
    private static final String PRINT_STATEMENT_REG_EX = "^\\s*print\\s+\\w+\\s*$";
    private static final String READ_STATEMENT_REG_EX = "^\\s*read\\s+\\w+\\s*$";
    private static final String GOTO_STATEMENT_REG_EX = "^\\s*goto\\s+\\w+\\b\\s*$";
    private static final String VARIABLE_ASSIGNMENT_STATEMENT_REG_EX = "^\\s*\\w+\\s+=\\s+\\w+\\s*$";
    private static final String VARIABLE_STATEMENT_REG_EX = "^\\s*\\w+\\s+=\\s+\\w+\\s+[*+-\\\\%]\\s+\\w+\\s*$";
    private static final String CONDITION_STATEMENT_REG_EX = "^\\s*if\\s+\\w+\\s+(>|<|==|!=|>=|<=)\\s+\\w+\\s+goto\\s+\\w+\\s*$";

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
     * Метод разбивает скрипт на отдельные команды по метке, если команда и метка
     * находятся на одной строке, либо по символам новой строки. Так же убираются все
     * лишние пробелы по краям и вся строка преобразуется в нижний регистр (скрипт не
     * чувствителен к регистру)
     */
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


    /**
     * цикл исполнения всех команд скрипта
     */
    public void executeScript() {
        while(currentStatement < statementList.size()) {
            parseStatementType(statementList.get(currentStatement));
        }
        System.out.println("end of script");
    }

    /**
     * С помощью регулярных выражений метод определяет тип команды и
     * вызывает соответствующий метод
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
        throw new IllegalScriptValueException("syntax in statement number " + currentStatement + " is not correct");
    }


    /**
     * Метод разбивает команду на отдельные аргументы,
     * в зависимости от оператора выполняет выражение из двух операндов
     * и присваивает значение результата выражения переменной
     * @param statement команда соответствующая регулярному выражению
     * {@link ScriptExecutor#VARIABLE_STATEMENT_REG_EX}
     */
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

    /**
     * переход к исполнению блока команд по метке из второго аргумента строки
     * @param statement команда соответствующая регулярному выражению
     * {@link ScriptExecutor#TAG_STATEMENT_REG_EX}
     */
    private void goToTag(String statement) {
        String[] parts = statement.split("\\s+");
        currentStatement = statementList.indexOf(parts[1]);
    }

    /**
     * инициализация переменной или перезапись значения переменной
     * @param statement команда соответствующая регулярному выражению
     * {@link ScriptExecutor#VARIABLE_ASSIGNMENT_STATEMENT_REG_EX}
     */
    private void assignVariableValue(String statement) {
        String[] parts = statement.split("\\s+");
        variables.put(parts[0], getOperandValue(parts[2]));
        currentStatement++;
    }

    /**
     * Считывает число введенное из командной строки и инициализирует
     * переменную или, в случае ее присутствия, присваивает ей новое значение.
     * @param statement команда соответствующая регулярному выражению
     * {@link ScriptExecutor#READ_STATEMENT_REG_EX}
     */
    private void readVariableValue(String statement) {
        String[] parts = statement.split("\\s+");
        System.out.print(parts[1] + "=");
        int value = inputScanner.nextInt();
        variables.put(parts[1], value);
        currentStatement++;
    }

    /**
     * Выводит на экран число или переменную
     * @param statement команда соответствующая регулярному выражению
     * {@link ScriptExecutor#PRINT_STATEMENT_REG_EX}
     */
    private void printVarNum(String statement) {
        String[] parts = statement.split("\\s+");
        System.out.println(getOperandValue(parts[1]));
        currentStatement++;
    }

    /**
     * проверяет условие, переданное в команде. В случае истинности
     * переходит к исполнению блока команд по метке из последнего
     * аргумента строки
     * @param statement команда соответствующая регулярному выражению
     * {@link ScriptExecutor#CONDITION_STATEMENT_REG_EX}
     */
    private void executeConditionalStatement(String statement) {
        String[] parts = statement.split(" ");
        boolean conditionTrue = checkCondition(parts);
        if(conditionTrue) {
            currentStatement = statementList.indexOf(parts[5]);
        } else {
            currentStatement++;
        }
    }

    /**
     * Проверка условия
     * @param parts аргументы строки условнойкоманды
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
     * Получение значения переменно или литерала
     * представленного в строке команды
     * @param varNum отдельный операнд команды
    */
    private int getOperandValue(String varNum) {
       if(varNum.matches("\\d+")) {
           return Integer.valueOf(varNum);
       } else {
           return variables.get(varNum);
       }
    }

}
