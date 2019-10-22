package com.sekhanov.scriptparser;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * ScriptParser
 */
public class ScriptParser {

    private BufferedReader br;

    public ScriptParser(BufferedReader br) {
        this.br = br;
    }

    public void testFile() {
        try {
            System.out.println(br.readLine());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
}
