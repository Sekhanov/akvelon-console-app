package com.sekhanov.scriptparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ScriptParser
 */
public class ScriptParser {

    private List<String> commandList;
    private Scanner fileScanner;
    private Scanner inputScanner;


    public ScriptParser(Scanner fileScanner) {
        this.fileScanner = fileScanner;
        inputScanner = new Scanner(System.in);
        commandList = new ArrayList<>();
    }

    public void testFile() {
            while(fileScanner.hasNextLine()) {
                System.out.println(fileScanner.nextLine());
            }

    }

    public void parseCommands() {
        String nextCommand = "";
        while(fileScanner.hasNext()) {
            fileScanner.useDelimiter("[\\n:]");
            nextCommand = fileScanner.next().trim().toLowerCase();
            if(nextCommand.equals("")) {
                continue;
            }
            commandList.add(nextCommand);
        }
        commandList.forEach(System.out::println);
    }

    


}
