package com.sekhanov.scriptparser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Hello world!
 */
public final class App {
    public static void main(String[] args) {
        try (Scanner fileScanner = new Scanner(new FileReader(args[0]))) {
            ScriptExecutor scriptExecutor = new ScriptExecutor(fileScanner);
            scriptExecutor.addStatements();
            scriptExecutor.startExecutingScript();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
