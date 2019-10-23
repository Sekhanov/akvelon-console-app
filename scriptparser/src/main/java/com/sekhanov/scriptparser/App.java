package com.sekhanov.scriptparser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Hello world!
 */
public final class App {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("enter one argument with script file location or file name if the file is in the same directory with jar file");
        } else {
            try (Scanner fileScanner = new Scanner(new FileReader(args[0]))) {
                ScriptExecutor scriptExecutor = new ScriptExecutor(fileScanner);
                // scriptExecutor.testFile();
                scriptExecutor.addStatements();
                scriptExecutor.executeScript();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
