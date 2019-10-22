package com.sekhanov.scriptparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Hello world!
 */
public final class App {
    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            ScriptParser scriptParser = new ScriptParser(br);
            scriptParser.testFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
