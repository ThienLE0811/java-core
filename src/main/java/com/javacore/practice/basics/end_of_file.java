package com.javacore.practice.basics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class end_of_file {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        // (1) bien dem dong. Sample output bat dau tu "1 Hello world" -> khoi tao bang may?
        int lineNumber = 1;
        
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            System.out.printf("%d %s%n", lineNumber, line);
            lineNumber++;
        }

        bufferedReader.close();
    }
}
